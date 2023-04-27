/*
 * MIT License
 *
 * Copyright (c) Copyright (c) 2023 Blamer.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.blamer.bot.answer.generator.impl;

import io.blamer.bot.answer.generator.MessageGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

/**
 * Start conversation.
 */
@Slf4j
@Component("/registry")
public class RegistryMessageGenerator implements MessageGenerator {

  /**
   * The command.
   */
  @Value("${bot.answers.registry.command}")
  private String command;

  /**
   * Command description.
   */
  @Value("${bot.answers.registry.description}")
  private String description;

  /*
   * @todo #19 After hub service will be created, redirect the token.
   *   Send auth message to hub.
   * */
  @Override
  public SendMessage messageFromUpdate(final Update update) {
    final String token = RegistryMessageGenerator.detachToken(update);
    return new SendMessage(
      update.getMessage().getChatId().toString(),
      "Registry token doesn't available yet, sorry. Your token: '%s'"
        .formatted(token)
    );
  }

  @Override
  public BotCommand messageAsBotCommand() {
    return new BotCommand(this.command, this.description);
  }

  /**
   * Detaches token form messages like this.
   * <pre>/registry token</pre>
   *
   * @param update The update to get token
   * @return Result as string
   */
  private static String detachToken(final Update update) {
    final String text = update.getMessage().getText();
    try {
      return text.split(" ")[1];
    } catch (final ArrayIndexOutOfBoundsException ex) {
      final String notFound = "Token not found in %s".formatted(text);
      RegistryMessageGenerator.log.debug(notFound);
      return notFound;
    }
  }
}