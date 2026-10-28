package br.com.soulprime.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro CORS: permite que o Front-end (React na Vercel) consuma a API
 * mesmo estando em outro dominio. As origens liberadas vem da variavel CORS_ORIGINS.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private final String origensPermitidas;

    public CorsFilter(@Value("${app.cors.origins:*}") String origensPermitidas) {
        this.origensPermitidas = origensPermitidas;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origem = request.getHeader("Origin");
        if (origensPermitidas.equals("*")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
        } else if (origem != null && origensPermitidas.contains(origem)) {
            response.setHeader("Access-Control-Allow-Origin", origem);
            response.setHeader("Vary", "Origin");
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Usuario-Id");
        response.setHeader("Access-Control-Expose-Headers", "Location");
        response.setHeader("Access-Control-Max-Age", "3600");

        //Requisicao de "pre-voo" (OPTIONS) do navegador: responde 200 sem chamar o endpoint
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        chain.doFilter(req, res);
    }
}
