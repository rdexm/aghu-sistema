package br.gov.mec.aghu.registrocolaborador.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.annotation.security.RolesAllowed;
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

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.service.BaseService;

//import com.wordnik.swagger.annotations.Api;
//import com.wordnik.swagger.annotations.ApiOperation;
//import com.wordnik.swagger.annotations.ApiParam;
//import com.wordnik.swagger.annotations.ApiResponse;
//import com.wordnik.swagger.annotations.ApiResponses;

@RequestScoped
@Path("/registrocolaborador")
@Produces("application/json")
@Api(value = "/registrocolaborador", description = "Fornece serviços para o módulo de registro de colaboradores")
public class RegistroColaboradorService extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1294710148867519812L;

	private static final Log LOG = LogFactory
			.getLog(RegistroColaboradorService.class);

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@GET	
	@RolesAllowed({"ADMIN_API","MED_API"})
	@Path("usuario/{login}")
	@Produces({"application/json"})
	@ApiOperation(value = "Retorna o usuário a partir do seu login", response=Usuario.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Informações básicas do usuário"),
							@ApiResponse(code = 404, message = "Registros não encontrados com o filtro informado")})
	public Response buscaUsuario(
			@ApiParam(value="Login do usuário", required=true) @PathParam("login") String login) {

		Response resposta = null;
		Usuario usuario = new Usuario();
		RapServidores rapServidor = null;
		try {
			rapServidor = this.registroColaboradorFacade
					.obterServidorAtivoPorUsuario(login);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao buscar usuário: "+login, e);
			resposta = Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
		}
		
		if (rapServidor != null) {
			usuario.setVinculo(rapServidor.getId().getVinCodigo());
			usuario.setMatricula(rapServidor.getId().getMatricula());
			usuario.setLogin(rapServidor.getUsuario());
			resposta = Response.status(Status.OK).entity(usuario).build();
		}

		return resposta;
	}
}