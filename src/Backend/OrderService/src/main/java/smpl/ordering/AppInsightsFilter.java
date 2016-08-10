package smpl.ordering;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.ExceptionHandledAt;
import com.microsoft.applicationinsights.telemetry.ExceptionTelemetry;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;

import org.springframework.stereotype.Component;

@Component
public class AppInsightsFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        TelemetryClient client = Utility.getTelemetryClient();

        if (client != null && client.getContext() != null && !Utility.isNullOrEmpty(client.getContext().getInstrumentationKey())) // && !client.isDisabled())
        {
            Date startTime = new Date();

            HttpServletRequest request = (HttpServletRequest) req;
            String method = request.getMethod();
            String rURI = request.getRequestURI();
            String scheme = request.getScheme();
            String host = request.getHeader("Host");
            String query = request.getQueryString();
            String session = request.getSession().getId();
            String name = request.getServletPath();

            RequestTelemetry telemetry = new RequestTelemetry(String.format("%s %s", method, rURI), startTime, 0L, "200", false);
            telemetry.setHttpMethod(method);
            telemetry.setTimestamp(startTime); // Doesn't work right now.
            telemetry.setName(name);

            if (!Utility.isNullOrEmpty(query))
            {
                telemetry.setUrl(String.format("%s://%s%s?%s", scheme, host, rURI, query));
            }
            else
            {
                telemetry.setUrl(String.format("%s://%s%s", scheme, host, rURI));
            }

            TelemetryContext ctx = client.getContext();

            if (!Utility.isNullOrEmpty(session))
            {
                ctx.getSession().setId(session);
            }

            ctx.getOperation().setId(telemetry.getId());
            ctx.getOperation().setName(telemetry.getName());

            try
            {
                chain.doFilter(req, res);

                Date endTime = new Date();

                HttpServletResponse response = (HttpServletResponse) res;

				Duration duration = new Duration(endTime.getTime() - startTime.getTime());
                telemetry.setDuration(duration);
                telemetry.setResponseCode(((Integer) response.getStatus()).toString());

                client.track(telemetry);

                // Clear the operation id.
                ctx.getOperation().setId(null);
                ctx.getOperation().setName(null);
            }
            catch (Exception exc)
            {
                Date endTime = new Date();

                ExceptionTelemetry ext = new ExceptionTelemetry(exc);
                ext.setExceptionHandledAt(ExceptionHandledAt.Platform);
                client.track(ext);

				Duration duration = new Duration(endTime.getTime() - startTime.getTime());
                telemetry.setDuration(duration);
                telemetry.setResponseCode("500");
                telemetry.setSuccess(false);

                client.track(telemetry);

                // Clear the operation id.
                ctx.getOperation().setId(null);
                ctx.getOperation().setName(null);
                
                throw exc;
            }
        }
        else
        {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy()
    {

    }
}
