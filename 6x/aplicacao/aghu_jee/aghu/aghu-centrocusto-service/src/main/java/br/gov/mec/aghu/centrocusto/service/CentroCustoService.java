package br.gov.mec.aghu.centrocusto.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.service.BaseService;
import br.gov.mec.aghu.service.centrocusto.CentroCusto;
import br.gov.mec.aghu.service.centrocusto.CentroCustoFiltro;

//import com.wordnik.swagger.annotations.Api;
//import com.wordnik.swagger.annotations.ApiOperation;
//import com.wordnik.swagger.annotations.ApiParam;
//import com.wordnik.swagger.annotations.ApiResponse;
//import com.wordnik.swagger.annotations.ApiResponses;

@RequestScoped
@Path("/centrocusto")
@Produces("application/json")
@Api(value = "/centrocusto", description = "Fornece serviços genéricos do módulo Centro de Custos do AGHU")
public class CentroCustoService extends BaseService {

	private static final long serialVersionUID = 2234744138867521842L;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@POST
	@RolesAllowed("{ADMIN_API}")
	@Path("busca/")
	@Produces({"application/json"})
	@ApiOperation(value = "Retorna o centro de custo pesquisado", response=List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Consulta OK"),
							@ApiResponse(code = 400, message = "Falta parâmetro")})
	public Response buscaCentroCusto(
			@ApiParam(value="Código do Centro de Custo", required=false) @FormParam("codigo") Integer codigo, 
			@ApiParam(value="Descrição do Centro de Custo", required=false) @FormParam("descricao") String descricao, 
			@ApiParam(value="Situação do Centro de Custo", required=false) @FormParam("situacao") String situacao) {
		
		Response resposta = null;
		if (codigo == null && descricao == null && situacao == null) {
			resposta = Response.status(Status.BAD_REQUEST).entity("Faltou informar filtro").build();
		} else {
			List<CentroCusto> listaCc = new ArrayList<CentroCusto>();
			CentroCustoFiltro c = new CentroCustoFiltro();
			c.setCodigo(codigo);
			c.setDescricao(descricao);
			c.setSituacao(situacao);
			List<FccCentroCustos> listaRetorno = centroCustoFacade.pesquisarCentroCustos(c);
			if (listaRetorno != null) {
				for(FccCentroCustos cc : listaRetorno) {
					CentroCusto cr = new CentroCusto();
					cr.setCodigo(cc.getCodigo());
					cr.setDescricao(cc.getDescricao());
					cr.setNomeReduzido(cc.getNomeReduzido());
					listaCc.add(cr);
				}
			}
			
			resposta = Response.status(Status.OK).entity(listaCc).build();
		}
		
		return resposta;
	}
}