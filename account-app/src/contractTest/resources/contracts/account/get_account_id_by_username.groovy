package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'Get account of username'
    name 'get_account_id_by_username'

    request {
        method GET()
        url '/accounts/account/user1'
        headers {
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),   // для консьюмера (WireMock): любой Bearer-токен
                    producer('Bearer test-token')  // для провайдера (MockMvc-тест): ровно этот токен
            )
        }
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1
        )
    }
}
