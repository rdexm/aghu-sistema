package br.gov.mec.aghu.api.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.service.BaseService;
import br.gov.mec.aghu.service.seguranca.BearerToken;

//import com.wordnik.swagger.annotations.Api;
//import com.wordnik.swagger.annotations.ApiOperation;
//import com.wordnik.swagger.annotations.ApiParam;
//import com.wordnik.swagger.annotations.ApiResponse;
//import com.wordnik.swagger.annotations.ApiResponses;

@RequestScoped
@Path("/api")
@Produces("application/json")
@Api(value = "/api", description = "Fornece serviços genéricos para API AGHU")
public class ApiService extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1294710148867519812L;

	@EJB
	private ICascaFacade cascaFacade;
	
	@GET
	@PermitAll
	@Path("auth/")
	@Produces({"application/json"})
	@ApiOperation(value = "Retorna um token de acesso a partir do login da aplicação", response=BearerToken.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Autenticação OK"),
							@ApiResponse(code = 401, message = "Falha na Autenticação")})
	public Response obterTokenAcesso(
			@ApiParam(value="Chave Autenticadora", required=true) @HeaderParam("Authorization") String authInfo,
			@ApiParam(value="Origem da Requisição", required=true) @Context HttpServletRequest request ) {
		
		Response resposta = null;
		
		BearerToken token = cascaFacade.obterBearerToken(authInfo, request.getRemoteAddr());
		
		if (token.getAcessToken() != null) {
			resposta = Response.status(Status.OK).entity(token).build();
		} else {
			resposta = Response.status(Status.UNAUTHORIZED).entity(token).build();			
		}
		
		return resposta;
	}
	
	@GET
	@PermitAll
	@Path("auth/refresh")
	@Produces({"application/json"})
	@ApiOperation(value = "Renova a data de expiração de um token de acesso a partir de um token de refresh", response=BearerToken.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Refresh OK"),
							@ApiResponse(code = 401, message = "Falha no refresh")})
	public Response refreshTokenAcesso(
			@ApiParam(value="Token de refresh", required=true) @HeaderParam("Authorization") String refreshToken,			
			@ApiParam(value="Origem da Requisição", required=true) @Context HttpServletRequest request) {
		
		Response resposta = null;
		
		BearerToken token = cascaFacade.refreshBearerToken(refreshToken, request.getRemoteAddr());
		
		if (token.getAcessToken() != null) {
			resposta = Response.status(Status.OK).entity(token).build();
		} else {
			resposta = Response.status(Status.UNAUTHORIZED).entity(token).build();			
		}
		
		return resposta;
	}
}