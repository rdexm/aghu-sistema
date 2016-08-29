package br.gov.mec.aghu.certificacaodigital.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;

//import com.wordnik.swagger.annotations.Api;
//import com.wordnik.swagger.annotations.ApiOperation;
//import com.wordnik.swagger.annotations.ApiParam;
//import com.wordnik.swagger.annotations.ApiResponse;
//import com.wordnik.swagger.annotations.ApiResponses;

@RequestScoped
@Path("/certificacaodigital")
@Produces({"application/json"})
@Api(value = "/certificacaodigital", description = "Fornece serviços para o módulo de certificação digital")
public class CertificacaoDigitalService {

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@GET
	@Path("/verificarpendencia")
	@Produces({"application/json"})
	@ApiOperation(value = "Verifica se servidor possui pendências de assinatura digital", response = Boolean.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Possui pendencia"),
							@ApiResponse(code = 406, message = "Não Possui habilitacao") })
	public Response existePendenciaAssinaturaDigital(
							@ApiParam(value="Identificador do atendimento", required=true) Integer seqAtendimento)  {
		Boolean possuiPendencia = this.certificacaoDigitalFacade.existePendenciaAssinaturaDigital(seqAtendimento);
		return Response.status(Status.OK).entity(possuiPendencia.toString()).build();
	}
	
	@POST
	@Path("/inativardocumentos")
	@Consumes({"application/json"})
	@Produces({"application/json"})
	@ApiOperation(value="Inativa versao de documentos", response = Status.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Operação Concluída")})
	public Response inativarVersaoDocumentos(
			@ApiParam(value="Lista de Identificadores de Documentos", required=true) List<Integer> listSeq) {
		this.certificacaoDigitalFacade.inativarVersaoDocumentos(listSeq);
		return Response.status(Status.OK).build();
	}
	
	@GET
	@Path("/verificarhabilitacao")
	@Produces({"application/json"})
	@ApiOperation(value = "Verifica se servidor possui habilitacao para certificação digital", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Status da habilitacao (true/false)") })	
	public Response verificarHabilitacao(
			@ApiParam(value="Vínculo do Usuário", required=true) Short vinCodigo,
			@ApiParam(value="Matrícula do Usuário", required=true) Integer matricula) {
		Boolean habilita = this.certificacaoDigitalFacade.verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado(matricula, vinCodigo);
		return Response.status(Status.OK).entity(habilita.toString()).build();
	}
}