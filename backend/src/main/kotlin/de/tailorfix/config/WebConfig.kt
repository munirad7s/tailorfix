package de.tailorfix.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.UUID

/**
 * Zalando RESTful API Guidelines: every request carries an `X-Flow-ID` header that is
 * propagated to downstream calls and logs. We generate one when the client does not send it.
 */
@Component
class FlowIdFilter : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val flowId = request.getHeader(FLOW_ID_HEADER)?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        MDC.put("flow_id", flowId)
        response.setHeader(FLOW_ID_HEADER, flowId)
        try {
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove("flow_id")
        }
    }

    companion object {
        const val FLOW_ID_HEADER = "X-Flow-ID"
    }
}

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .exposedHeaders(FlowIdFilter.FLOW_ID_HEADER, "Location")
    }
}
