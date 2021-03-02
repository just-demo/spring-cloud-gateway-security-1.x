package demo.security;

import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

public class Oauth2ClientContextFilterWithPath extends OAuth2ClientContextFilter {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    protected void redirectUser(UserRedirectRequiredException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UriComponentsBuilder builder = fromUriString(e.getRedirectUri());
        e.getRequestParams().forEach(builder::queryParam);
        if (e.getStateKey() != null) {
            builder.queryParam("state", e.getStateKey());
        }

        String url = getBaseUrl(request) + builder.build().encode().toUriString();
        redirectStrategy.sendRedirect(request, response, url);
    }

    @Override
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    private String getBaseUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        return url.substring(0, url.length() - request.getRequestURI().length() + request.getContextPath().length());
    }
}
