package com.example.invoices.util;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class RequestLogEnhancer {

    private static final Logger logger = LoggerFactory.getLogger( RequestLogEnhancer.class );

    public static Request enhance( Request request ) {
        StringBuilder output = new StringBuilder();

        request.onRequestBegin( req -> {
            output.append( "Request method: " ).append( req.getMethod() ).append( ", URL: " ).append( req.getURI().toString() ).append( "\n" );
        } );

        request.onRequestContent( ( req, content ) -> {
            output.append( "Body|Payload: " ).append( toString( content, getCharset( req.getHeaders() ) ) );
        } );

        request.onRequestSuccess( req -> {
            logger.info( output.toString() );
            output.delete( 0, output.length() );
        } );

        return request;
    }

    private static String toString( ByteBuffer buffer, Charset charset ) {
        byte[] bytes;
        if ( buffer.hasArray() ) {
            bytes = new byte[ buffer.capacity() ];
            System.arraycopy( buffer.array(), 0, bytes, 0, buffer.capacity() );
        } else {
            bytes = new byte[ buffer.remaining() ];
            buffer.get( bytes, 0, bytes.length );
        }
        return new String( bytes, charset );
    }

    private static Charset getCharset( HttpFields headers ) {
        String contentType = headers.get( HttpHeader.CONTENT_TYPE );
        if ( contentType != null ) {
            String[] tokens = contentType
                    .toLowerCase( Locale.US )
                    .split( "charset=" );
            if ( tokens.length == 2 ) {
                String encoding = tokens[ 1 ].replaceAll( "[;\"]", "" );
                return Charset.forName( encoding );
            }
        }
        return StandardCharsets.UTF_8;
    }
}
