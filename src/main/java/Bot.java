import Tests.Tests;
import UserService.DataService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private static final String GREETING_MESSAGE = "\uD83E\uDD73Вас вітає Тренер-бот з цифрової грамотності!✨\uD83D\uDC68\u200D\uD83C\uDFEB\n\n" +
            "Призначення цього бота - ознайомити з поняттям \"цифрова грамотність\",\uD83D\uDE2E та його складовими:\n" +
            " - Розв'язання проблем у цифровому середовищі та навчання впродовж життя\uD83E\uDDD1\u200D\uD83C\uDF93\uD83D\uDCDA\n" +
            " - Безпека в цифровому середовищі\uD83D\uDCBB\uD83D\uDD10\n" +
            " - Створення цифрового контенту\uD83C\uDFAE\uD83D\uDCF1 \n" +
            " - Комунікація та взаємодія у цифровому суспільстві\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66\uD83D\uDCAC\n" +
            " - Інформаційна грамотність, уміння працювати з даними\uD83D\uDCCA\uD83E\uDD16\n" +
            " - Основи комп'ютерної грамотності\uD83D\uDDA5\uD83D\uDCAA\n\n" +
            "Для перегляду усіх команд використовуйте\uD83D\uDC49 /help";

    private static final String HELP_MESSAGE = "Пояснення до кнопок:\n\n" +
            "✏️\uD83D\uDCD5 Встановити кількість тестів: n - встановити кількість тестів, що видає бот. n має бути в межах від 5 до 20 включно\n\n" +
            "\uD83E\uDD49\uD83E\uDD13 Легкі тести* - отримати випадкові тести початкової складності\n\n" +
            "\uD83E\uDD48\uD83E\uDD78 Середні тести - отримати випадкові тести середьої складності\n\n" +
            "\uD83E\uDD47\uD83D\uDC68\u200D\uD83C\uDF93 Складні тести - отримати випадкові тести високої складності\n\n" +
            "\uD83D\uDCEC\uD83D\uDDDE Підписатися на розсилку - увімкнути розсилання корисного контенту(новин, статей, сповіщень про нові можливості бота тощо)\n\n" +
            "\uD83D\uDCEB\uD83D\uDE45\u200D♂️ Відписатися від розсилки - вимкнути розсилання корисного контенту(новин, статей, сповіщень про нові можливості бота тощо)";


    private SendMessage getResponseMessage(Message message) throws TelegramApiException {
        switch (message.getText()) {
            case "/start":
                return sendMessage(message, GREETING_MESSAGE);
            case "/help":
            case "Допомога":
                return sendMessage(message, HELP_MESSAGE);
            case "Легкі тести":
                return getEasyTests(message);
            case "Середні тести":
                return getMediumTests(message);
            case "Складні тести":
                return getHardTests(message);
            case "Підписатися на розсилку":
                return turnOnSendingUsefulContent(message);
            case "Відписатися від розсилки":
                return turnOffSendingUsefulContent(message);
            default:
                return defaultResponse(message);
        }
    }

    private SendMessage defaultResponse(Message message) throws TelegramApiException {
        String messageText = message.getText();
        Long chatId = message.getChatId();
        if (messageText.contains("Встановити кількість тестів:")) {
            try {
                int amountOfTests = Integer.parseInt(messageText.substring(29));
                if (amountOfTests >= 5 && amountOfTests <= 20) {
                    setNumberOfTests(chatId, amountOfTests);
                    return new SendMessage(chatId.toString(), "✅Успішно встановлено");
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new SendMessage(chatId.toString(), "❗️Уведіть після команди через пробіл число від 5 до 20!");
            }
        } else if (messageText.contains("/broadcast") && chatId == 519257550) {
            String broadcastMessage = messageText.replaceFirst("/broadcast ", "");
            List<String> chatIdList = DataService.getChatIdFromBroadcastList();
            for (String id : chatIdList) {
                if (!broadcastMessage.equals("")) {
                    execute(new SendMessage(id, broadcastMessage));
                } else if (message.getPhoto() != null) {
                    execute(new SendPhoto(id, (InputFile) message.getPhoto()));
                }
            }
            return null;
        }
        return new SendMessage(chatId.toString(), "Команда не розпізнана\uD83D\uDDFF\uD83E\uDD37\u200D♂️");
    }

    private SendMessage turnOnSendingUsefulContent(Message message) {
        DataService.addChatIdToBroadcastList(message.getChatId().toString());
        return new SendMessage(message.getChatId().toString(), "\uD83C\uDF89Ви успішно підпісалися на розсилку новин!\uD83C\uDF81");
    }

    private SendMessage turnOffSendingUsefulContent(Message message) {
        DataService.removeChatIdFromBroadcastList(message.getChatId().toString());
        return new SendMessage(message.getChatId().toString(), "❌Ви успішно відписалися від розсилки новин!\uD83D\uDC94");
    }

    private void setNumberOfTests(Long chatId, int amountOfTests) {
        String data = chatId + ":" + amountOfTests + "\n";
        DataService.addChatIdAmountOfTests(data);
    }

    private SendMessage getEasyTests(Message message) throws TelegramApiException {
        sendQuiz(message, Tests.easyTests);
        return null;
    }

    private SendMessage getMediumTests(Message message) throws TelegramApiException {
        sendQuiz(message, Tests.mediumTests);
        return null;
    }

    private SendMessage getHardTests(Message message) throws TelegramApiException {
        sendQuiz(message, Tests.hardTests);
        return null;
    }

    private void sendQuiz(Message message, List<String> testList) throws TelegramApiException {
        Long chatId = message.getChatId();
        int amountOfTests = DataService.getAmountOfTestsFromChatId(chatId);
        List<String> randomList = new ArrayList<>(testList);
        List<String> resultList = new ArrayList<>(amountOfTests);
        if (amountOfTests < testList.size()) {
            while (randomList.size() != amountOfTests) {
                randomList.remove((int) (Math.random() * randomList.size()));
            }
        }
        int index = 0;
        while (resultList.size() < amountOfTests) {
            int randomIndex =  (int) (Math.random() * randomList.size());
            resultList.add(index++, randomList.get(randomIndex));
            randomList.remove(randomIndex);
        }

        for (String quizText : resultList) {
            String question = quizText.substring(quizText.indexOf("<q>") + 3, quizText.indexOf("<a>"));
            String correctOption = quizText.substring(quizText.indexOf("<a>") + 3, quizText.indexOf("<o>"));
            List<String> options = new ArrayList<>(Arrays.asList(quizText.substring(quizText.indexOf("<o>") + 3).split("<o>")));
            int answerId = (int) (Math.random() * options.size() + 1);
            options.add(answerId, correctOption);

            SendPoll sendPoll = new SendPoll();
            sendPoll.setType("quiz");
            sendPoll.setChatId(chatId.toString());
            sendPoll.setQuestion(question);
            sendPoll.setOptions(options);
            sendPoll.setCorrectOptionId(answerId);
            execute(sendPoll);
        }
    }

    private SendMessage sendMessage(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        return sendMessage;
    }

    private static void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Легкі тести"));
        keyboardRow1.add(new KeyboardButton("Середні тести"));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton("Складні тести"));
        keyboardRow2.add(new KeyboardButton("Допомога"));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton("Підписатися на розсилку"));
        keyboardRow3.add(new KeyboardButton("Відписатися від розсилки"));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            Message message = update.getMessage();
            try {
                SendMessage sendMessage = getResponseMessage(message);
                if (sendMessage != null) {
                    setButtons(sendMessage);
                    execute(sendMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "digital_literacy_bot";
    }

    @Override
    public String getBotToken() {
        return "5066028407:AAGUKBAGGV49MgHnTJGtzLAU5KeasqRqsh0";
    }
}
