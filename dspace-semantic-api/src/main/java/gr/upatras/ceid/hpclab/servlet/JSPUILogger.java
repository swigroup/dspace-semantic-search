package gr.upatras.ceid.hpclab.servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class JSPUILogger
{
    private static Logger log = Logger.getLogger(JSPUILogger.class);
    
    public static void logException(String msgError, HttpServletRequest request, Exception exception)
    {
        log.error(msgError, exception);
        request.setAttribute("error", msgError + ": " + exception.getMessage());
    }
}
package gr.upatras.ceid.hpclab.servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class JSPUILogger
{
    private static Logger log = Logger.getLogger(JSPUILogger.class);
    
    public static void logException(String msgError, HttpServletRequest request, Exception exception)
    {
        log.error(msgError, exception);
        request.setAttribute("error", msgError + ": " + exception.getMessage());
    }
}
package gr.upatras.ceid.hpclab.servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class JSPUILogger
{
    private static Logger log = Logger.getLogger(JSPUILogger.class);
    
    public static void logException(String msgError, HttpServletRequest request, Exception exception)
    {
        log.error(msgError, exception);
        request.setAttribute("error", msgError + ": " + exception.getMessage());
    }
}
