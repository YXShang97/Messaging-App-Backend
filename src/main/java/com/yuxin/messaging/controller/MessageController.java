package com.yuxin.messaging.controller;

import com.yuxin.messaging.annotation.NeedAuthentication;
import com.yuxin.messaging.enums.MessageType;
import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.model.Message;
import com.yuxin.messaging.model.User;
import com.yuxin.messaging.response.CommonResponse;
import com.yuxin.messaging.response.ListMessagesResponse;
import com.yuxin.messaging.service.MessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/messages")
@Log4j2
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/sendMessage")
    @NeedAuthentication
    public CommonResponse sendMessage(User user,
                                      @RequestParam(required = false) Integer receiverId,
                                      @RequestParam(required = false) Integer groupChatId,
                                      @RequestParam(required = true) MessageType messageType,
                                      @RequestParam(required = false) String content,
                                      @RequestParam(required = false) MultipartFile file) throws Exception {
        this.messageService.sendMessage(user, receiverId, groupChatId, messageType, content, file.getInputStream());
        return new CommonResponse(Status.OK);
    }

    @GetMapping("/listMessages")
    @NeedAuthentication
    public ListMessagesResponse listMessages(User user,
                                             @RequestParam(required = false) Integer receiverId,
                                             @RequestParam(required = false) Integer groupChatId,
                                             @RequestParam(required = false) Integer page) throws Exception {
        List<Message> messages = this.messageService.listMessages(user, receiverId, groupChatId, page);
        return ListMessagesResponse.builder()
                                   .messages(messages)
                                   .build();
    }

    @GetMapping("/file")
    @NeedAuthentication
    public ResponseEntity<ByteArrayResource> getFile(User user, @RequestParam Integer messageId) throws MessagingServiceException {
        byte[] bytes = this.messageService.getMessageFile(user, messageId);
        // TODO: set content type
        ResponseEntity<ByteArrayResource> responseEntity = ResponseEntity.ok()
                .contentLength(bytes.length)
                .body(new ByteArrayResource(bytes));
        return responseEntity;
    }

    // Long Polling
    @GetMapping("/listLatestMessages")
    public DeferredResult<List<Integer>> getLatestMessages(@RequestParam Integer latestReceivedMessageId) {

        DeferredResult<List<Integer>> deferredResult = new DeferredResult<>(5000L, List::of);

        CompletableFuture.runAsync(() -> {
            var random = new Random();
            while (true) {
                int rand = random.nextInt();
                System.out.println(rand);
                if (rand % 100 == 0) {
                    deferredResult.setResult(List.of(10));
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception exception) {
                }
            }
        });

        log.info("defered result returned");
        return deferredResult;
    }
}
