package br.gov.mec.aghu.administracao.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioReindexaveis;
import br.gov.mec.aghu.service.BaseService;
import br.gov.mec.aghu.sistema.bussiness.ISistemaFacade;
import br.gov.mec.aghu.sistema.bussiness.UserSessions.UserSession;

//import com.wordnik.swagger.annotations.Api;
//import com.wordnik.swagger.annotations.ApiOperation;
//import com.wordnik.swagger.annotations.ApiParam;
//import com.wordnik.swagger.annotations.ApiResponse;
//import com.wordnik.swagger.annotations.ApiResponses;

@RequestScoped
@Path("/administracao")
@Produces("application/json")
@Api(value = "/administracao", description = "Fornece serviços genéricos para Administração do AGHU")
public class AdministracaoService extends BaseService {

	private static final long serialVersionUID = 1294710148867521842L;
	
	private static final Log LOG = LogFactory.getLog(AdministracaoService.class);

	@EJB
	private ISistemaFacade sistemaFacade;

	@GET
	@PermitAll
	@Path("lucene/reindexar/{pojo}")
	@Produces({"application/json"})
	@ApiOperation(value = "Retorna o status da operação de reindexação", response=String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Reindexação OK"),
							@ApiResponse(code = 400, message = "Falta parâmetro"),
							@ApiResponse(code = 417, message = "Falha na Reindexação")})
	public Response reindexar(@ApiParam(value="Pojo para Indexar", required=true) @PathParam("pojo") String pojo) {

		Response resposta = null;
		if (pojo == null) {
			resposta = Response.status(Status.BAD_REQUEST).entity("Faltou informar o pojo para indexação").build();
		} else {
			if (DominioReindexaveis.TODOS.name().equals(pojo.toUpperCase())) {
				try {
					for (DominioReindexaveis reindexando : DominioReindexaveis.values()) {
						if (DominioReindexaveis.TODOS.equals(reindexando)) {
							continue;
						}
						sistemaFacade.indexar(Class.forName(reindexando.toString()));
					}
					resposta = Response.status(Status.OK).entity("Todas classes reindexadas com sucesso!").build();
				} catch (ClassNotFoundException e) {
					LOG.error(e.getMessage(), e);
					resposta = Response.status(Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					resposta = Response.status(Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
				}
			} else {
				try {
					DominioReindexaveis reindexar = DominioReindexaveis.valueOf(pojo.toUpperCase()); 
					sistemaFacade.indexar(Class.forName(reindexar.toString()));
					resposta = Response.status(Status.OK).entity("Classe "+reindexar.getDescricao() +" reindexada com sucesso!").build();
				} catch (ClassNotFoundException e) {
					LOG.error(e.getMessage(), e);
					resposta = Response.status(Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					resposta = Response.status(Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
				}
			}
		}
		return resposta;
	}
	
	@GET
	@PermitAll
	@Path("session/user/list")
	@Produces({ "application/json" })
	@ApiOperation(value = "Retorna a lista de usuários logados na instancia do servidor corrente")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Listagem OK"),
							@ApiResponse(code = 417, message = "Erro na consulta") })
	public Response listarUsuariosLogados() {
		Response resposta = null;
		try {
			Map<String, UserSession> listaUsuarios = sistemaFacade.listarUsuariosLogados();
			resposta = Response.status(Status.OK).entity(listaUsuarios).build();
		} catch (Exception e) {
			resposta = Response.status(Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
		}
		return resposta;

	}
}