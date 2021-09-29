package jpabook.jpastore.web.api;

import jpabook.jpastore.application.OrderService;
import jpabook.jpastore.dto.order.OrderSimpleRequestDto;
import jpabook.jpastore.application.dto.order.OrderListResponseDto;
import jpabook.jpastore.application.dto.order.OrderResponseDto;
import jpabook.jpastore.application.dto.order.OrderSimpleDto;
import jpabook.jpastore.dto.order.OrderItemAddDto;
import jpabook.jpastore.web.response.ResultResponse;
import jpabook.jpastore.web.response.ResponseMessage;
import jpabook.jpastore.web.response.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderApiController {

    private final OrderService orderService;

    @PostMapping("/api/v1/order")
    public ResponseEntity<ResultResponse<?>> createOrderV1(@RequestBody OrderSimpleRequestDto requestDto){
        Long createdOrderId = orderService.order(requestDto.getMemberId(),
                requestDto.getItemId(), requestDto.getQuantity());

        Map<String, Long> data = new HashMap<>();
        data.put("created_order_id", createdOrderId);

        return ResponseEntity.created(URI.create("/api/v1/order"))
                .body(ResultResponse.res(StatusCode.OK, ResponseMessage.CREATED_ORDER, data));
    }

    @GetMapping("/api/v1/order/{id}")
    public ResponseEntity<ResultResponse<?>> getOrderById(@PathVariable(name = "id") Long orderId){
        OrderResponseDto data = orderService.findOrderById(orderId);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDER, data));
    }

    /**
     *  Member, Delivery(Lazy Loading) 정보 포함한 단일 simple 주문 조회.
     *  페치조인을 통한 조회.
     */
    @GetMapping("/api/v2/simple-order/{id}")
    public ResponseEntity<ResultResponse<?>> getOrderWithMemberDelivery(@PathVariable(name = "id") Long orderId){
        OrderSimpleDto data = orderService.findOrderByIdWithMemberDelivery(orderId);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDER, data));
    }

    @GetMapping("/api/v1/orders")
    public ResponseEntity<ResultResponse<?>> getOrders_V1(){
        OrderListResponseDto<?> data = orderService.findAllOrders();

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }

    /**
     *  전체 주문 조회 with 컬렉션(orderItems) : 페치조인 사용.
     */
    @GetMapping("/api/v2/orders")
    public ResponseEntity<ResultResponse<?>> getOrders_V2() {
        OrderListResponseDto<?> data = orderService.findAllWithItems();

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }

    /**
     *  페치 조인 사용한 컬렉션 조회 with 'distinct' 키워드.
     */
    @GetMapping("/api/v2.1/orders")
    public ResponseEntity<ResultResponse<?>> getOrders_V2_distinct() {
        OrderListResponseDto<?> data = orderService.findAllWithItemsDistinct();

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }

    @GetMapping("/api/v3/orders")
    public ResponseEntity<ResultResponse<?>> getOrders_V3() {
        OrderListResponseDto<?> data = orderService.findAllWithItemsDistinct();

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }

    @GetMapping("/api/v3.1/orders")
    public ResponseEntity<ResultResponse<?>> getOrders_V3_paging(@RequestParam(value = "offset",defaultValue = "0") int offset,
                                                                 @RequestParam(value = "limit", defaultValue = "100") int limit) {
        OrderListResponseDto<?> data = orderService.findAllWithItemsPaging(offset, limit);

        return ResponseEntity.ok(ResultResponse.res(StatusCode.OK, ResponseMessage.READ_ORDERS, data));
    }

    @GetMapping("/api/v4/orders")
    public ResponseEntity<ResultResponse<?>> findOrdersV4() {
        OrderListResponseDto<?> data = orderService.findAllByDto();

        return new ResponseEntity(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ORDERS, data), HttpStatus.OK);
    }

    @GetMapping("/api/v5/orders")
    public ResponseEntity<ResultResponse<?>> findOrdersV5() {
        OrderListResponseDto data = orderService.findAllByDto_optimazation();

        return new ResponseEntity(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ORDERS, data), HttpStatus.OK);
    }

    /**
     *  Member, Delivery 정보 포함한 simple 전체 주문 조회.
     *  페치조인을 통한 조회.
     */
    @GetMapping("/api/v2/simple-orders")
    public ResponseEntity findSimpleOrdersV2(){
        List<OrderSimpleDto> data = orderService.findAllWithMemberDelivery();

        return new ResponseEntity(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ORDERS, data), HttpStatus.OK);
    }

    /**
     *  JPA에 DTO 바로 조회.
     */
    @GetMapping("/api/v3/simple-orders")
    public ResponseEntity findSimpleOrdersV3(){
        OrderListResponseDto data = orderService.findOrderDtos();

        return new ResponseEntity(ResultResponse.res(StatusCode.OK,
                ResponseMessage.READ_ORDERS, data), HttpStatus.OK);
    }

    @PutMapping("/api/v1/add-orderitem")
    public ResponseEntity addOrderItemV1(@RequestBody OrderItemAddDto requestDto) {
        OrderResponseDto data = orderService.addOrderItems(requestDto.getOrderId(),
                requestDto.getItemId(), requestDto.getQuantity());

        return new ResponseEntity(ResultResponse.res(StatusCode.OK,
                ResponseMessage.UPDATED_ORDER, data), HttpStatus.OK);
    }

}
