package br.gov.mec.aghu.core.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import br.gov.mec.aghu.core.commons.Parametro;

/**
 * @author dlaks
 * 
 *      gmneto -   A versão original desta classe, que serviu de base para esta, usa o
 *         seam mail para criar os emails. É preciso avaliar uma solução
 *         alternativa.
 * 
 */
public class EmailUtil implements Serializable {

	private static final long serialVersionUID = -520826407924558421L;

	private EmailInformation emailInformation;
	
	@Inject @Parametro("mail_session_host")
	private String hostName;

	private static final Log LOG = LogFactory.getLog(EmailUtil.class);
	
	public void enviaEmail(String remetente, String destinatario,
			String destinatarioOculto, String assunto, String conteudo,
			AnexoEmail... anexos) {
		List<ContatoEmail> destinatarios = new ArrayList<ContatoEmail>(1);
		if (destinatario != null) {
			destinatarios.add(new ContatoEmail(destinatario));
		}

		List<ContatoEmail> destinatariosOcultos = new ArrayList<ContatoEmail>(1);
		if (destinatarioOculto != null) {
			destinatariosOcultos.add(new ContatoEmail(destinatarioOculto));
		}

		this.enviaEmail(new ContatoEmail(remetente), destinatarios,
				destinatariosOcultos, assunto, conteudo, anexos);
	}

	
	public void enviaEmail(ContatoEmail remetente, ContatoEmail destinatario,
			ContatoEmail destinatarioOculto, String assunto, String conteudo,
			AnexoEmail... anexos) {
		List<ContatoEmail> destinatarios = new ArrayList<ContatoEmail>(1);
		if (destinatario != null) {
			destinatarios.add(destinatario);
		}

		List<ContatoEmail> destinatariosOcultos = new ArrayList<ContatoEmail>(1);
		if (destinatarioOculto != null) {
			destinatariosOcultos.add(destinatarioOculto);
		}

		this.enviaEmail(remetente, destinatarios, destinatariosOcultos,
				assunto, conteudo, anexos);
	}

	
	private void enviaEmail(String remetente, List<String> destinatarios,
			List<String> destinatariosOcultos, String assunto, String conteudo,
			Boolean isHtml, AnexoEmail... anexos) {
		ContatoEmail auxRemetente = new ContatoEmail(remetente);
		List<ContatoEmail> auxDestinatarios = new ArrayList<ContatoEmail>();
		if (destinatarios != null) {
			for (String auxDdestinatario : destinatarios) {
				auxDestinatarios.add(new ContatoEmail(auxDdestinatario));
			}
		}

		List<ContatoEmail> auxDestinatariosOcultos = new ArrayList<ContatoEmail>();
		if (destinatariosOcultos != null) {
			for (String auxDestinatarioOculto : destinatariosOcultos) {
				auxDestinatariosOcultos.add(new ContatoEmail(
						auxDestinatarioOculto));
			}
		}

		if (isHtml){
			this.enviaEmailHtml(auxRemetente, auxDestinatarios,
					auxDestinatariosOcultos, assunto, conteudo, anexos);
		}
		else {
		    this.enviaEmail(auxRemetente, auxDestinatarios,
				auxDestinatariosOcultos, assunto, conteudo, anexos);
		}
	}
	
	public void enviaEmail(String remetente, List<String> destinatarios,
			List<String> destinatariosOcultos, String assunto, String conteudo,
			AnexoEmail... anexos) {
		this.enviaEmail(remetente, destinatarios, destinatariosOcultos, assunto, conteudo, false, anexos);
	}

	
	public void enviaEmailHtml(String remetente, List<String> destinatarios,
			List<String> destinatariosOcultos, String assunto, String conteudo,
			AnexoEmail... anexos) {
		this.enviaEmail(remetente, destinatarios, destinatariosOcultos, assunto, conteudo, true, anexos);
	}

	public void enviaEmailHtml(ContatoEmail remetente,
			List<ContatoEmail> destinatarios,
			List<ContatoEmail> destinatariosOcultos, String assunto,
			String conteudo, AnexoEmail... anexos) {
		try {			
			
			emailInformation = new EmailInformation(remetente, destinatarios,
					destinatariosOcultos, assunto, conteudo, anexos);
			
			HtmlEmail email;
			
			email = new HtmlEmail();
						
			if (!ArrayUtils.isEmpty(anexos)){
				for (AnexoEmail anexoEmail : anexos){					
					 email.attach(new ByteArrayDataSource(anexoEmail.getByteArray(), "application/pdf"), anexoEmail.getFileName(), "anexo");
				}
			}
						
			// Utilize o hostname do seu provedor de email
			email.setHostName(hostName);
			// Adicione os destinatários
			for (ContatoEmail destinatario: destinatarios){
				email.addTo(destinatario.getEmail(), destinatario.getNome());
			}
			// Configure o seu email do qual enviará
			email.setFrom(remetente.getEmail(), remetente.getNome());
			// Adicione um assunto
			email.setSubject(assunto);
			// Adicione a mensagem do email
			email.setHtmlMsg("<html>"+ conteudo + "</html>");	
			email.setTextMsg("Seu servidor de e-mail não suporta mensagem HTML");
			// Para autenticar no servidor é necessário chamar os dois métodos
			// abaixo
//			System.out.println("autenticando...");
//			email.setSSL(true);
//			email.setAuthentication("username", "senha");
//			System.out.println("enviando...");
			email.send();
			
		} catch (Exception e) {
			LOG.error("Erro ao enviar o e-mail de " + remetente + " para "
					+ listaDestinatarios(destinatarios), e);
		}
	}
	
	
	
	
	public void enviaEmail(ContatoEmail remetente,
			List<ContatoEmail> destinatarios,
			List<ContatoEmail> destinatariosOcultos, String assunto,
			String conteudo, AnexoEmail... anexos) {
		try {			
			
			emailInformation = new EmailInformation(remetente, destinatarios,
					destinatariosOcultos, assunto, conteudo, anexos);
			
			Email email;
						
			if (ArrayUtils.isEmpty(anexos)){
				email = new SimpleEmail();
			}else{
				
				email =  new MultiPartEmail();
				for (AnexoEmail anexoEmail : anexos){
					((MultiPartEmail) email).attach(new ByteArrayDataSource(anexoEmail.getByteArray(), "application/pdf"), anexoEmail.getFileName(), "anexo");
				}
			}
						
			// Utilize o hostname do seu provedor de email
			email.setHostName(hostName);
			// Adicione os destinatários
			for (ContatoEmail destinatario: destinatarios){
				email.addTo(destinatario.getEmail(), destinatario.getNome());
			}
			// Configure o seu email do qual enviará
			email.setFrom(remetente.getEmail(), remetente.getNome());
			// Adicione um assunto
			email.setSubject(assunto);
			// Adicione a mensagem do email
			email.setMsg(conteudo);
			// Para autenticar no servidor é necessário chamar os dois métodos
			// abaixo
//			System.out.println("autenticando...");
//			email.setSSL(true);
//			email.setAuthentication("username", "senha");
//			System.out.println("enviando...");
			email.send();
			
		} catch (Exception e) {
			LOG.error("Erro ao enviar o e-mail de " + remetente + " para "
					+ listaDestinatarios(destinatarios), e);
		}
	}

	private String listaDestinatarios(List<ContatoEmail> destinatarios) {
		StringBuffer sb = new StringBuffer();

		for (ContatoEmail destinatario : destinatarios) {
			if (sb.length() > 0) {
				sb.append(',');
			}

			sb.append(destinatario);
		}

		return sb.toString();
	}

	

	public EmailInformation getEmailInformation() {
		return emailInformation;
	}

	public void setEmailInformation(EmailInformation emailInformation) {
		this.emailInformation = emailInformation;
	}

}
