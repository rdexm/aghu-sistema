package br.gov.mec.aghu.compras.autfornecimento.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.AutorizacaoFornecimentoEmailAtrasoON.AutorizacaoFornecimentoEmailAtrasoONMessageCode;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoItemFornecedorVO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.mail.AnexoEmail;
import br.gov.mec.aghu.core.mail.EmailUtil;

@Stateless
public class AutorizacaoFornecimentoEmailAtrasoRN extends BaseBusiness {
	private static final long serialVersionUID = -8555252476286602895L;
	
	private static final Log LOG = LogFactory.getLog(AutorizacaoFornecimentoEmailAtrasoRN.class);
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;

	@Inject
	private EmailUtil emailUtil;

	public List<AutorizacaoFornecimentoItemFornecedorVO> listarAFsMateriaisParaEmailAtraso(List<ParcelasAFPendEntVO> afsSelecionadas) {
		
		List<AutorizacaoFornecimentoItemFornecedorVO> listaMateriaisEmailParcial = new ArrayList<AutorizacaoFornecimentoItemFornecedorVO>();
		for (ParcelasAFPendEntVO parcelasAFPendEntVO : afsSelecionadas) {
			Integer numeroAFN = parcelasAFPendEntVO.getNumeroAFN();
			Integer numeroAFP = parcelasAFPendEntVO.getNumeroAFP();
			listaMateriaisEmailParcial.addAll(getScoAutorizacaoFornDAO().listarAFsMateriaisParaEmailAtraso(numeroAFN, numeroAFP));
		}
		return listaMateriaisEmailParcial;
	}
	
	public void enviarEmailFornecedor(AutorizacaoFornecimentoEmailVO dadosEmail) throws ApplicationBusinessException {
		String remetente = dadosEmail.getEmailUsuarioLogado();//"jenkins.ctis@gmail.com"; //email do usuario logado
		List<String> destinatariosOcultos = null;
		List<String> destinatarios = new ArrayList<String>();
		String assunto = dadosEmail.getEmailAssunto();
		
		if(dadosEmail.getEnviarFornecedor() && dadosEmail.getEmailFornecedor() != null) {
			destinatarios.add(dadosEmail.getEmailFornecedor());
		}
		
		if(dadosEmail.getEnviarUsuarioLogado() && dadosEmail.getEmailUsuarioLogado() != null) {
			destinatarios.add(dadosEmail.getEmailUsuarioLogado());
		}
		
		if(dadosEmail.getEnviarCopia() && dadosEmail.getEmailCopia() != null) {
			destinatarios.add(dadosEmail.getEmailCopia());
		}

		if(destinatarios.size() > 0) {
			if(dadosEmail.getAnexarPlanilha()) {
				String nomeArquivo = gerarNomeArquivoAnexoFornecedor(dadosEmail.getRazaoSocialFornecedor());
				AnexoEmail anexoEmail = new AnexoEmail(nomeArquivo, dadosEmail.getPlanilhaAnexo(), "application/xls");
				getEmailUtil().enviaEmail(remetente, destinatarios, destinatariosOcultos, assunto, dadosEmail.getTextoEmailHTML(), anexoEmail);
			} else {
				getEmailUtil().enviaEmail(remetente, destinatarios, destinatariosOcultos, assunto, dadosEmail.getTextoEmailHTML());
			}
			if(dadosEmail.getMostrarMensagem()) {
				throw new ApplicationBusinessException(AutorizacaoFornecimentoEmailAtrasoONMessageCode.EMAIL_ENVIADO_SUCESSO, Severity.INFO);
			}
		} else {
			throw new ApplicationBusinessException(AutorizacaoFornecimentoEmailAtrasoONMessageCode.EMAIL_SELECIONE_NOTIFICACAO, Severity.ERROR);
		}
		
	}
	
	private String gerarNomeArquivoAnexoFornecedor(String razaoSocial) {
		DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmm");
		StringBuilder nomeArquivoAnexoFornecedor = new StringBuilder();
		nomeArquivoAnexoFornecedor.append(razaoSocial).append('_')
		.append(dateFormat.format(new Date()))
		.append(".csv");
		return nomeArquivoAnexoFornecedor.toString();
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}

	protected void setScoAutorizacaoFornDAO(ScoAutorizacaoFornDAO scoAutorizacaoFornDAO) {
		this.scoAutorizacaoFornDAO = scoAutorizacaoFornDAO;
	}

	protected void setEmailUtil(EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}

	protected EmailUtil getEmailUtil() {
		return emailUtil;
	}
	
	
	
}