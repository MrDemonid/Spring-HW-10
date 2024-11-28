package mr.demonid.service.payment.services;

import mr.demonid.service.payment.domain.Payment;
import mr.demonid.service.payment.dto.PaymentRequest;
import mr.demonid.service.payment.dto.UserPayInfo;
import mr.demonid.service.payment.exceptions.PaymentException;
import mr.demonid.service.payment.links.UserServiceClient;
import mr.demonid.service.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class PaymentServiceIntegrationTest {

    private static final long userId = 1L;
    private static final long recipientId = 2L;
    private static final long accountId = 1L;

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    private PaymentRepository paymentRepository;

    private PaymentRequest paymentRequest = new PaymentRequest(UUID.randomUUID(), userId, recipientId, BigDecimal.valueOf(100), "DEBIT");
    private UserPayInfo userPayInfo = new UserPayInfo(userId, accountId, BigDecimal.valueOf(500));


    /**
     * Тест проверки возможности трансфера средств.
     */
    @Test
    public void testCheckTransferTest() {
        when(userServiceClient.getAccount(paymentRequest.getFromUserId())).thenReturn(ResponseEntity.ok(userPayInfo));
        /*
            Должно завершиться без исключений. Корректность вызова исключений при ошибках
            мы проверили в модульных тестах.
         */
        assertDoesNotThrow(() -> paymentService.checkTransfer(paymentRequest));
    }

    /**
     * Тест трансфера средств.
     */
    @Test
    public void testTransferTest() throws PaymentException {
        when(userServiceClient.transaction(paymentRequest)).thenReturn(ResponseEntity.ok().build());

        Payment payment = new Payment(paymentRequest.getOrderId(), paymentRequest.getFromUserId(),
                                      paymentRequest.getRecipientId(), paymentRequest.getTransferAmount(),
                                      paymentRequest.getType(), LocalDateTime.now(), "Pending");
        when(paymentRepository.findById(paymentRequest.getOrderId())).thenReturn(Optional.of(payment));
        /*
            Должно завершиться без исключений. Корректность вызова исключений при ошибках
            мы проверили в модульных тестах.
         */
        assertDoesNotThrow(() -> paymentService.transfer(paymentRequest));
    }
}
