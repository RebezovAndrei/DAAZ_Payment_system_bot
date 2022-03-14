package org;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.google.cloud.functions.*;
import com.google.cloud.storage.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DaazNotifierBotLauncher implements HttpFunction {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String getBid;
        String getPercent;
        //Создаем объект класса parser и парсим ставку и процент
        Parser parser = new Parser("BROWSER COOKIE");
        {
            try {
                getPercent = parser.percent();
                getBid = parser.bid();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //Открываем блоб, в котором хранится процентная ставка, с которой будем сравнивать сравку
        Storage storage = StorageOptions.getDefaultInstance().getService();

        String bucketName = "BUCKET NAME";
        String blobName = "FILE IN BUCKET NAME";
        BlobId blobId = BlobId.of(bucketName, blobName);
        byte[] content = storage.readAllBytes(blobId);
        StringBuilder percentInFile = new StringBuilder();

        for (int i = 0; i < content.length; i++) {
            percentInFile.append((char) content[i]);
        }

        BufferedWriter writer = httpResponse.getWriter();
        writer.write("I did it!");

        //Если процентная ставка не совпадает с процентной ставкой записанной в файл (т.е. процентная ставка изменилась, то отправляем сообщение в телегу)
        if (!percentInFile.toString().equals(getPercent)) {

            //Перезаписываем файл с процентной ставкой, если процентная ставка изменилась
            Blob blob = storage.get(blobId);
            WritableByteChannel channel = blob.writer();
            channel.write(ByteBuffer.wrap(getPercent.getBytes(UTF_8)));
            channel.close();

            //Создаем бот который будет отсылать уведомление об изменении процентов
            PercentBot bot = new PercentBot(new DefaultBotOptions());
            try {
                bot.execute(SendMessage.builder().chatId("PASTE HERE YOR CHAT ID").text(getBid + "\nПроцент по картам: " + getPercent).build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
