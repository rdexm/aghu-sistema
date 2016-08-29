package br.gov.mec.aghu.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Date;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
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
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Classe responsável pela implementação dos serviços usados no processo de assinatura digital. 
 * Esses serviços são acessados através da applet, que executa a assinatura do documento propriamente dita na máquina cliente.
 * 
 * @author aghu
 *
 */
@Path("assinaturaDigital")
public class AssinaturaDigitalServices {

	private static final Log LOG = LogFactory.getLog(AssinaturaDigitalServices.class);

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFace = ServiceLocator
			.getBean(ICertificacaoDigitalFacade.class, "aghu-certificacaodigital");
	
	/**
	 * Atualiza a base de dados com o documento devidamente assinado.
	 * 
	 * @param envelope
	 * @param id
	 * @return
	 */
	@PermitAll
	@POST
	@Path("envelope/{id}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response atualizarEnvelope(String envelopeFormatoBase64, @PathParam("id") String id) {
		Response resposta = null;
		LOG.debug("Envelope formato base64 recebido:\n"+envelopeFormatoBase64);
		
		byte[] envelopeDecodificado = Base64.decodeBase64(envelopeFormatoBase64);

		try {
			certificacaoDigitalFace.atualizarEnvelope(Integer.valueOf(id), envelopeDecodificado);
			resposta = Response.status(Status.CREATED).build();
		} catch (Exception e) {
			LOG.error("Erro atualizar envelope: " + e.getMessage(), e);
			resposta = Response.serverError().build();
		}

		return resposta;
	}
	
	@PermitAll
	@POST
	@Path("certificado/valida")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response validarCadeiaCertificado(String certificadoPEM) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("chamou validarCadeiaCertificado");
			LOG.debug("recebeu o certificado PEM:\n" + certificadoPEM);
		}
		
		Certificate[] certs = converterPEMparaCadeiaCertificados(certificadoPEM);
		
		Boolean retorno = false;
		Response resposta = null;

		try {
			if (certs.length > 0 ) {
				retorno = certificacaoDigitalFace.valida(certs);
			}
			
			// usado status pois o retorno não estava sendo recebido pelo client qdo post
			// 
			if (retorno){
				resposta = Response.status(Status.OK).build();
			} else {
				resposta = Response.status(Status.NOT_ACCEPTABLE).build();
			}
			
//			resposta = Response.status(Status.OK).entity(retorno.toString()).build();
		} catch (Exception e) {
			LOG.error("Erro realizar validacoes da cadeia de certificados", e);
			resposta = Response.status(Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).build();
		}

		return resposta;
	}
	
	/**
	 * Valida se o usuário do cartão tem permissão para executar a assinatura,
	 * consistindo vínculo e matrícula, bem como valida se suas crendenciais 
	 * são válidas.
	 * 
	 * @param cpf CPF do usuário do cartão
	 * @param usuario usuário logado
	 * @param vinculo vínculo do servidor responsável pelo documento
	 * @param matricula matrícula do servidor responsável pelo documento
	 *  
	 * @return true caso servidor tenha permissão para assinar, false caso contrário
	 */
	@PermitAll
	@GET
	@Path("usuario/responsavel/permite/{cpf}/{usuario}/{vinculo}/{matricula}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response validarCertificadoParaServidor(@PathParam("cpf") String cpf, @PathParam("usuario") String usuario, 
			@PathParam("vinculo") Short vinculo, @PathParam("matricula") Integer matricula) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("chamou validarServidorCertificado para CPF|VÍNCULO|MATRÍCULA: " + cpf + "|" + vinculo + "|" + matricula);
		}
		
		try {

			Boolean result = this.certificacaoDigitalFace.permite(cpf, vinculo, matricula, usuario);

			return Response.status(Status.OK).entity(result.toString()).build();
		} catch (Exception e) {
			LOG.error("Erro ", e);
			return Response.serverError().build();
		}
		
	}	
	
	/**
	 * Retorna o documento a ser assinado
	 *  
	 * @param id Identificador do documento
	 * @return Documento a ser assinado
	 */
	@PermitAll
	@GET
	@Path("documento/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response obterArquivo(@PathParam("id") Integer id) {
		LOG.debug("chamou obterArquivo " + id);

		byte[] documento = null;
		Response resposta;

		try {
			documento = certificacaoDigitalFace.buscarAghVersaoDocumento(id).getOriginal();
			
			ResponseBuilder rb = Response.ok(documento, MediaType.APPLICATION_OCTET_STREAM);
			rb.header("Content-Disposition", "attachment; filename=" + "documento.pdf"); // deixar com esse nome?
			
			resposta = rb.status(Status.OK).build();
		} catch (Exception e) {
			LOG.error("Erro buscar AGH_VERSOES_DOCUMENTOS: "+e.getMessage(), e);
			resposta = Response.serverError().build();
		}

		return resposta;
	}
	
	/**
	 * Retorna a data/hora do servidor no formato ISO8601 (yyyyMMdd'T'HHmmss)
	 * Exemplo de retorno para o dia 1/Março/2015 14:05:06:
	 * 20150301T140506
	 *  
	 * @param id Identificador do documento
	 * @return Timestamp no formato ISO8601
	 */
	@PermitAll
	@GET
	@Path("timestamp")
	@Produces(MediaType.TEXT_PLAIN)
	public Response obterDataHoraServidor() {
		LOG.debug("chamou obterDataAtual");

		Response resposta;

		try {
			String dataHoraFormatoISO8601 = DateFormatUtils.format(new Date(), "yyyyMMdd'T'HHmmss");

			resposta = Response.status(Status.OK).entity(dataHoraFormatoISO8601).build();
		} catch (Exception e) {
			LOG.error("Erro obter data e hora corrente do servidor de aplicação: " + e.getMessage(), e);
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