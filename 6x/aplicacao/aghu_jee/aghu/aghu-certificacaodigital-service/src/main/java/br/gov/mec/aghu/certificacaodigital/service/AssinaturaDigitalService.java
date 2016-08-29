package br.gov.mec.aghu.certificacaodigital.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Date;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.service.BaseService;

//import com.wordnik.swagger.annotations.Api;
//import com.wordnik.swagger.annotations.ApiOperation;
//import com.wordnik.swagger.annotations.ApiParam;
//import com.wordnik.swagger.annotations.ApiResponse;
//import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Classe responsável pela implementação dos serviços usados no processo de
 * assinatura digital. Esses serviços são acessados através da applet, que
 * executa a assinatura do documento propriamente dita na máquina cliente.
 * 
 * @author aghu
 *
 */
@RequestScoped
@Path("/certificacaodigital/assinaturadigital")
@Api(value="/certificacaodigital/assinaturadigital", description = "Fornece serviços para o módulo de assinatura digital da certificação digital")
@Produces({"application/json"})
public class AssinaturaDigitalService extends BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3128151337408362183L;

	private static final Log LOG = LogFactory
			.getLog(AssinaturaDigitalService.class);

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	/**
	 * Atualiza a base de dados com o envelope criptográfico de assinatura.
	 * 
	 * @param envelope
	 * @param id
	 * @return
	 */
	@POST
	@Path("envelope/{id}/{envelopeFormatoBase64}")
	@Consumes({"application/json"})
	@Produces({"application/json"})
	@ApiOperation(value="Atualiza a base de dados com o envelope criptográfico de assinatura.", response = Status.class)
	@ApiResponses(value = { @ApiResponse(code = 417, message = "Erro atualizar envelope"),
							@ApiResponse(code = 200, message = "Atualização OK")})
	public Response atualizarEnvelope(
		@ApiParam(value="Envelope criptográfico de assinatura", required=true) @PathParam("envelopeFormatoBase64") String envelopeFormatoBase64,
		@ApiParam(value="ID", required=true) @PathParam("id") String id) {
		Response resposta = null;
		LOG.debug("Envelope formato base64 recebido:\n" + envelopeFormatoBase64);

		byte[] envelopeDecodificado = Base64
				.decodeBase64(envelopeFormatoBase64);

		try {
			certificacaoDigitalFacade.atualizarEnvelope(Integer.valueOf(id),
					envelopeDecodificado);
			resposta = Response.status(Status.OK).build();
		} catch (Exception e) {
			LOG.error("Erro atualizar envelope: " + e.getMessage(), e);
			resposta = Response.status(Status.EXPECTATION_FAILED).build();
		}

		return resposta;
	}

	@POST
	@Path("certificado/valida/{certificadoPEM}")
	@Consumes({"application/json"})
	@Produces({"application/json"})
	@ApiOperation(value="Valida a cadeia de certificado.", response = Status.class)
	@ApiResponses(value = { @ApiResponse(code = 406, message = "Validação NOK"),
							@ApiResponse(code = 503, message = "Erro realizar validacoes da cadeia de certificados"),
							@ApiResponse(code = 200, message = "Validação OK")})
	public Response validarCadeiaCertificado(
			@ApiParam(value="Certificado que será validado", required=true) @PathParam("certificadoPEM") String certificadoPEM) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("chamou validarCadeiaCertificado");
			LOG.debug("recebeu o certificado PEM:\n" + certificadoPEM);
		}

		Certificate[] certs = converterPEMparaCadeiaCertificados(certificadoPEM);

		Boolean retorno = false;
		Response resposta = null;

		try {
			if (certs.length > 0) {
				retorno = certificacaoDigitalFacade.valida(certs);
			}

			// usado status pois o retorno não estava sendo recebido pelo client
			// qdo post
			//
			if (retorno) {
				resposta = Response.status(Status.OK).build();
			} else {
				resposta = Response.status(Status.NOT_ACCEPTABLE).build();
			}

		} catch (Exception e) {
			LOG.error("Erro realizar validacoes da cadeia de certificados", e);
			resposta = Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(e.getMessage()).build();
		}

		return resposta;
	}

	/**
	 * Valida se o usuário do cartão tem permissão para executar a assinatura,
	 * consistindo vínculo e matrícula, bem como valida se suas crendenciais são
	 * válidas.
	 * 
	 * @param cpf
	 *            CPF do usuário do cartão
	 * @param usuario
	 *            usuário logado
	 * @param vinculo
	 *            vínculo do servidor responsável pelo documento
	 * @param matricula
	 *            matrícula do servidor responsável pelo documento
	 * 
	 * @return true caso servidor tenha permissão para assinar, false caso
	 *         contrário
	 */
	@GET		
	@Path("usuario/responsavel/permite/{cpf}/{usuario}/{vinculo}/{matricula}")
	@ApiOperation(value="Valida se o usuário do cartão tem permissão para executar a assinatura, consistindo vínculo e matrícula, bem como valida se suas crendenciais são válidas.", response = Boolean.class)
	@ApiResponses(value = { @ApiResponse(code = 406, message = "Validação NOK"), 
							@ApiResponse(code = 200, message = "Validação OK")})
	public Response validarCertificadoParaServidor(
		@ApiParam(value="CPF do usuário", required=true) @PathParam("cpf") String cpf,
		@ApiParam(value="Login do Usuário", required=true) @PathParam("usuario") String usuario,
		@ApiParam(value="Vínculo do Usuário", required=true) @PathParam("vinculo") Short vinculo,
		@ApiParam(value="Matrícula do Usuário", required=true) @PathParam("matricula") Integer matricula) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("chamou validarServidorCertificado para CPF|VÍNCULO|MATRÍCULA: "
					+ cpf + "|" + vinculo + "|" + matricula);
		}

		try {

			Boolean result = this.certificacaoDigitalFacade.permite(cpf,
					vinculo, matricula, usuario);

			return Response.status(Status.OK).entity(result.toString()).build();
		} catch (Exception e) {
			LOG.error("Erro ", e);
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}

	}

	/**
	 * Retorna o documento a ser assinado
	 * 
	 * @param id
	 *            Identificador do documento
	 * @return Documento a ser assinado
	 */
	@GET
	@Path("documento/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value="Retorna o documento a ser assinado.", response = Status.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Erro buscar AGH_VERSOES_DOCUMENTOS"), 
							@ApiResponse(code = 200, message = "Achou o arquivo")})	
	public Response obterArquivo(
			@ApiParam(value="ID do documento", required=true) @PathParam("id") Integer id) {
		LOG.debug("chamou obterArquivo " + id);

		byte[] documento = null;
		Response resposta;

		try {
			documento = certificacaoDigitalFacade.buscarAghVersaoDocumento(id)
					.getOriginal();

			ResponseBuilder rb = Response.ok(documento,
					MediaType.APPLICATION_OCTET_STREAM);
			rb.header("Content-Disposition", "attachment; filename="
					+ "documento.pdf"); // deixar com esse nome?

			resposta = rb.status(Status.OK).build();
		} catch (Exception e) {
			LOG.error("Erro buscar AGH_VERSOES_DOCUMENTOS: " + e.getMessage(),
					e);
			resposta = Response.status(Status.NOT_FOUND).build();
		}

		return resposta;
	}

	/**
	 * Retorna a data/hora do servidor no formato ISO8601 (yyyyMMdd'T'HHmmss)
	 * Exemplo de retorno para o dia 1/Março/2015 14:05:06: 20150301T140506
	 * 
	 * @param id
	 *            Identificador do documento
	 * @return Timestamp no formato ISO8601
	 */
	@GET
	@Path("timestamp")
	@Produces({"application/json"})
	@ApiOperation(value = "Retorna a data/hora do servidor", notes = "Retorna a data/hora do servidor no formato ISO8601 (yyyyMMdd'T'HHmmss)", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Data/Hora do Servidor")})	
	public Response obterDataHoraServidor() {
		LOG.debug("chamou obterDataAtual");

		Response resposta;

		try {
			String dataHoraFormatoISO8601 = DateFormatUtils.format(new Date(),
					"yyyyMMdd'T'HHmmss");

			resposta = Response.status(Status.OK)
					.entity(dataHoraFormatoISO8601).build();
		} catch (Exception e) {
			LOG.error(
					"Erro obter data e hora corrente do servidor de aplicação: "
							+ e.getMessage(), e);
			resposta = Response.serverError().build();
		}

		return resposta;
	}

	private Certificate[] converterPEMparaCadeiaCertificados(
			final String certificados) {
		Collection<? extends Certificate> certificates = null;

		try {
			CertificateFactory certFactory = CertificateFactory
					.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(certificados.getBytes());
			certificates = certFactory.generateCertificates(in);
		} catch (CertificateException e) {
			LOG.error("Erro ler o certificado digital: " + e.getMessage(), e);
		}

		return certificates.toArray(new Certificate[0]);
	}
}