package br.gov.mec.aghu.registrocolaborador.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.service.BaseService;
import br.gov.mec.aghu.service.paginacao.PaginatedResult;
import br.gov.mec.aghu.service.paginacao.PaginationInfo;
import br.gov.mec.aghu.service.registrocolaborador.vo.InstQualificadora;

//import com.wordnik.swagger.annotations.Api;
//import com.wordnik.swagger.annotations.ApiOperation;
//import com.wordnik.swagger.annotations.ApiParam;
//import com.wordnik.swagger.annotations.ApiResponse;
//import com.wordnik.swagger.annotations.ApiResponses;

@RequestScoped
@Path("/registrocolaborador/cadastrosbasicos")
@Produces({"application/json"})
@Api(value="/registrocolaborador/cadastrosbasicos", description = "Fornece serviços para o módulo de cadastros do registro de colaboradores")
public class CadastrosBasicosService extends BaseService {

	private static final long serialVersionUID = 2128151337408369189L;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	@GET
	@Path("instituicoes")
	@ApiOperation(value="Retorna as Instituições Qualificadoras com o filtro especificado.", response = PaginatedResult.class)
	@Produces({"application/json"})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Lista de instituições qualificadoras")})
	public Response buscaInstQualificadora(
			@ApiParam(value="Codigo") @QueryParam("codigo") Integer codigo,
			@ApiParam(value="Descrição") @QueryParam("descricao") String descricao,
			@ApiParam(value="Interno", allowableValues="[S,N]") @QueryParam("interno") @DefaultValue("N") String interno,
			@ApiParam(value="usoGPPG", allowableValues="[S,N]") @QueryParam("usogppg") @DefaultValue("N") String usoGppg,
			@ApiParam(value="Primeiro registro da página", required=true) @QueryParam("firstresult") Integer firstResult,
			@ApiParam(value="Número de registros na página", defaultValue = "10", required=true) @DefaultValue("10") @QueryParam("maxresults") Integer maxResults,
			@ApiParam(value="Propriedade que será ordenada", required=false) @QueryParam("orderproperty") String orderProperty,
			@ApiParam(value="Ordem ascendente ou descendente", defaultValue="asc", required=false, allowableValues="['asc','desc']") @DefaultValue("asc") @QueryParam("ordertype") String orderType) {
		
		List<InstQualificadora> result = null;
		Long count = this.cadastrosBasicosFacade.pesquisarInstituicaoQualificadoraCount(codigo,
				descricao, DominioSimNao.valueOf(interno), usoGppg.equalsIgnoreCase("S"));
		
		List<RapInstituicaoQualificadora> instituicaoQualificadora = this.cadastrosBasicosFacade
				.pesquisarInstituicaoQualificadora(codigo, descricao,
						DominioSimNao.valueOf(interno), usoGppg.equalsIgnoreCase("S"),
						firstResult, maxResults, orderProperty, (orderProperty == null) ? true : orderProperty.equalsIgnoreCase("asc"));

		result = new ArrayList<InstQualificadora>();
		for (RapInstituicaoQualificadora inst : instituicaoQualificadora) {
			result.add(new InstQualificadora(inst.getCodigo(),
					inst.getDescricao(), inst.getIndInterno().isSim()));
		}
		
		PaginatedResult<InstQualificadora> resultado = new PaginatedResult<InstQualificadora>();
		
		resultado.setPaginationInfo(new PaginationInfo(firstResult, maxResults, orderProperty, (orderProperty == null) ? true : orderProperty.equalsIgnoreCase("asc"), count));
		resultado.setListaItens(result);
		super.preencherLinksNavegacao(resultado);
		return Response.status(Status.OK).entity(resultado).build();
	}
}