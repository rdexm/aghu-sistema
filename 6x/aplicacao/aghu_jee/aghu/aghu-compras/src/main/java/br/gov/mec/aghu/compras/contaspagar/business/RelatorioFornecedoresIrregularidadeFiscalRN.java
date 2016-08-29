package br.gov.mec.aghu.compras.contaspagar.business;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorIrregularFiscalDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioEmail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedorIrregularFiscal;
import br.gov.mec.aghu.model.ScoFornecedorIrregularFiscalId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 *
 */
@Stateless
public class RelatorioFornecedoresIrregularidadeFiscalRN extends BaseBusiness {

	/** Identificador Único */
	private static final long serialVersionUID = -672983689223133538L;

	/** Constante de log */
	private static final Log LOG = LogFactory.getLog(RelatorioFornecedoresIrregularidadeFiscalRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EmailUtil emailUtil;

	@Inject
	private FcpTituloDAO tituloDAO;

	@Inject
	private ScoFornecedorIrregularFiscalDAO scoFornecedorIrregularFiscalDAO;
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;
	
	@Inject
	private ScoContatoFornecedorDAO scoContatoFornecedorDAO;
	
	
	private enum RelatorioFornecedoresIrregularidadeFiscalRNException implements BusinessExceptionCode {
		MENSAGEM_ERRO_DATA_INICIAL_MAIOR_DATA_FINAL_IRREGULARIDADE_FISCAL;
	}
	
	public List<DatasVencimentosFornecedorVO> pesquisarFornecedoresIrregularidadeFiscal(Date dataInicial,
			Date dataFinal, Integer numero) throws ApplicationBusinessException {
		
		if ((dataInicial != null && dataFinal != null) && DateUtil.validaDataMaior(dataInicial, dataFinal)) {
			throw new ApplicationBusinessException(RelatorioFornecedoresIrregularidadeFiscalRNException.MENSAGEM_ERRO_DATA_INICIAL_MAIOR_DATA_FINAL_IRREGULARIDADE_FISCAL);
		}
		List<DatasVencimentosFornecedorVO> listRetorno = this.tituloDAO.pesquisarFornecedoresIrregularidadeFiscal(dataInicial, dataFinal, numero);
		for (DatasVencimentosFornecedorVO item : listRetorno) {
			List<ScoFornecedorIrregularFiscal> listaFornIrregulares = this.scoFornecedorIrregularFiscalDAO.pesquisarHistoricoEmailsPorFornecedor(item.getNumero());
			
			if (!listaFornIrregulares.isEmpty()) {
				ScoFornecedorIrregularFiscal fornIrregular = listaFornIrregulares.get(0);
				
				if (fornIrregular.getSituacao().equals(DominioSituacaoEnvioEmail.OK)) {
					item.setComunicados(DateUtil.obterDataFormatada(fornIrregular.getData(), "dd/MM/yy"));
					
				} else {
					item.setComunicados("Erro");
				}
				String hintComunicado = "";
				for (ScoFornecedorIrregularFiscal fi : listaFornIrregulares) {
					String data = DateUtil.obterDataFormatada(fi.getData(), "dd/MM/yy HH:mm");
					String usuario = fi.getServidor().getPessoaFisica().getNome();
					String msg = fi.getSituacao().getDescricao();
					
					hintComunicado = hintComunicado.concat(data).concat(" - ").concat(usuario).concat(" - ").concat(msg).concat("<br/>");
				}
				item.setHintComunicados(hintComunicado);
			}
		}
		return listRetorno;
	}
	
	public void enviarEmailFornecedoresIrregularidadeFiscal(List<DatasVencimentosFornecedorVO> listaFornecedores) throws ApplicationBusinessException {
		
		for (DatasVencimentosFornecedorVO item : listaFornecedores) {
			ScoFornecedor fornecedor = scoFornecedorDAO.obterPorChavePrimaria(item.getNumero());
			
			List<ScoContatoFornecedor> listaContatos = scoContatoFornecedorDAO.obterContatosParaReceberEmailFornecedor(fornecedor);
			if (!listaContatos.isEmpty()) {
				AghParametros paramSetorLicitacoes = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_SETOR_LICITACOES);
				AghParametros paramHU = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);
				AghParametros paramRazaoSocial = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
				AghParametros paramEndLogradouro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_LOGRADOURO);
				AghParametros paramLocalLicitacoes = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LOCAL_LICITACOES);
				AghParametros paramEndCidade = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE);
				AghParametros paramUFSigla = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_HOSPITAL_UF_SIGLA);
				AghParametros paramEndCEP = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CEP);
				AghParametros paramEndFone = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_FONE);
				AghParametros paramHospFax = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_HOSPITAL_FAX);
				AghParametros paramHospSite = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_HOSPITAL_SITE);
				String dtValInss = obterDataString(item.getDataValidadeInss());
				String dtValRecFed = obterDataString(item.getDataValidadeRecFed());
				String dtValFgts = obterDataString(item.getDataValidadeFgts());
				
				for (ScoContatoFornecedor contato : listaContatos) {
					String remetente = paramSetorLicitacoes.getVlrTexto();
					String assunto = paramHU.getVlrTexto().concat(" - ").concat("Certidões Negativas Atualizadas");
					String razaoSocial = fornecedor.getRazaoSocial();
					String cpfCnpj = getCpfCnpjFormatado(fornecedor);
					String tipoEmail = verificarConteudoEmail(fornecedor.getCgc(), item);
					String conteudo = "";
					String email = "";
					
					switch (tipoEmail) {
					case "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_COMPLETO":
						conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_FORN_IRREG_FISCAL_COMPLETO");
						email = MessageFormat.format(conteudo, razaoSocial, cpfCnpj, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramLocalLicitacoes.getVlrTexto(), dtValInss, dtValRecFed, dtValFgts, paramRazaoSocial.getVlrTexto(),
								paramEndLogradouro.getVlrTexto(), paramEndCidade.getVlrTexto(), paramUFSigla.getVlrTexto(), paramEndCEP.getVlrTexto(),
								paramEndFone.getVlrTexto(), paramHospFax.getVlrTexto(), paramHospSite.getVlrTexto());
						break;
						
					case "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_INSS":
						conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_FORN_IRREG_FISCAL_INSS");
						email = MessageFormat.format(conteudo, razaoSocial, cpfCnpj, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramLocalLicitacoes.getVlrTexto(), dtValInss, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramEndCidade.getVlrTexto(), paramUFSigla.getVlrTexto(), paramEndCEP.getVlrTexto(), paramEndFone.getVlrTexto(),
								paramHospFax.getVlrTexto(), paramHospSite.getVlrTexto());
						break;
						
					case "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_REC_FED":
						conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_FORN_IRREG_FISCAL_REC_FED");
						email = MessageFormat.format(conteudo, razaoSocial, cpfCnpj, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramLocalLicitacoes.getVlrTexto(), dtValRecFed, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramEndCidade.getVlrTexto(), paramUFSigla.getVlrTexto(), paramEndCEP.getVlrTexto(), paramEndFone.getVlrTexto(),
								paramHospFax.getVlrTexto(), paramHospSite.getVlrTexto());
						break;
						
					case "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_FGTS":
						conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_FORN_IRREG_FISCAL_FGTS");
						email = MessageFormat.format(conteudo, razaoSocial, cpfCnpj, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramLocalLicitacoes.getVlrTexto(), dtValFgts, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramEndCidade.getVlrTexto(), paramUFSigla.getVlrTexto(), paramEndCEP.getVlrTexto(), paramEndFone.getVlrTexto(),
								paramHospFax.getVlrTexto(), paramHospSite.getVlrTexto());
						break;
						
					case "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_INSS_REC_FED":
						conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_FORN_IRREG_FISCAL_INSS_REC_FED");
						email = MessageFormat.format(conteudo, razaoSocial, cpfCnpj, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramLocalLicitacoes.getVlrTexto(), dtValInss, dtValRecFed, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramEndCidade.getVlrTexto(), paramUFSigla.getVlrTexto(), paramEndCEP.getVlrTexto(), paramEndFone.getVlrTexto(),
								paramHospFax.getVlrTexto(), paramHospSite.getVlrTexto());
						break;
						
					case "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_INSS_FGTS":
						conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_FORN_IRREG_FISCAL_INSS_FGTS");
						email = MessageFormat.format(conteudo, razaoSocial, cpfCnpj, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramLocalLicitacoes.getVlrTexto(), dtValInss, dtValFgts, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramEndCidade.getVlrTexto(), paramUFSigla.getVlrTexto(), paramEndCEP.getVlrTexto(), paramEndFone.getVlrTexto(),
								paramHospFax.getVlrTexto(), paramHospSite.getVlrTexto());
						break;
						
					case "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_REC_FED_FGTS":
						conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_FORN_IRREG_FISCAL_REC_FED_FGTS");
						email = MessageFormat.format(conteudo, razaoSocial, cpfCnpj, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramLocalLicitacoes.getVlrTexto(), dtValRecFed, dtValFgts, paramRazaoSocial.getVlrTexto(), paramEndLogradouro.getVlrTexto(),
								paramEndCidade.getVlrTexto(), paramUFSigla.getVlrTexto(), paramEndCEP.getVlrTexto(), paramEndFone.getVlrTexto(),
								paramHospFax.getVlrTexto(), paramHospSite.getVlrTexto());
						break;
					}
					
					emailUtil.enviaEmail(remetente, contato.getEMail(), null, assunto, email);
					
					inserirFornecedorIrregularFiscal(item.getNumero(), DominioSituacaoEnvioEmail.OK);
				}
			} else {
				inserirFornecedorIrregularFiscal(item.getNumero(), DominioSituacaoEnvioEmail.SE);
			}
		}
	}
	
	private String obterDataString(Date data) {
		return (data != null ? DateUtil.obterDataFormatada(data, "dd/MM/yy") : "");
	}
	
	private String verificarConteudoEmail(Long cgc, DatasVencimentosFornecedorVO vo) {
		if (exibirDtValInss(cgc, vo) && exibirDtValRecFed(vo) && exibirDtValFgts(cgc, vo)) {
			return "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_COMPLETO";
			
		} else if (exibirDtValInss(cgc, vo) && !exibirDtValRecFed(vo) && !exibirDtValFgts(cgc, vo)) {
			return "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_INSS";
			
		} else if (!exibirDtValInss(cgc, vo) && exibirDtValRecFed(vo) && !exibirDtValFgts(cgc, vo)) {
			return "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_REC_FED";
			
		} else if (!exibirDtValInss(cgc, vo) && !exibirDtValRecFed(vo) && exibirDtValFgts(cgc, vo)) {
			return "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_FGTS";
			
		} else if (exibirDtValInss(cgc, vo) && exibirDtValRecFed(vo) && !exibirDtValFgts(cgc, vo)) {
			return "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_INSS_REC_FED";
			
		} else if (exibirDtValInss(cgc, vo) && !exibirDtValRecFed(vo) && exibirDtValFgts(cgc, vo)) {
			return "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_INSS_FGTS";
			
		} else if (!exibirDtValInss(cgc, vo) && exibirDtValRecFed(vo) && exibirDtValFgts(cgc, vo)) {
			return "CONTEUDO_EMAIL_FORN_IRREG_FISCAL_REC_FED_FGTS";
			
		}
		return "";
	}
	
	private boolean exibirDtValInss(Long cgc, DatasVencimentosFornecedorVO vo) {
		Date dataAtual = DateUtil.truncaData(new Date());
		Date dtValInss = vo.getDataValidadeInss() != null ? DateUtil.truncaData(vo.getDataValidadeInss()) : dataAtual;
		boolean isDtValInssVencida = DateUtil.validaDataMenor(dtValInss, dataAtual);
		
		return (cgc != null && (isDtValInssVencida || vo.getDataValidadeInss() == null));
	}
	
	private boolean exibirDtValRecFed(DatasVencimentosFornecedorVO vo) {
		Date dataAtual = DateUtil.truncaData(new Date());
		Date dtValRecFed = vo.getDataValidadeRecFed() != null ? DateUtil.truncaData(vo.getDataValidadeRecFed()) : dataAtual;
		boolean isDtValRecFedVencida = DateUtil.validaDataMenor(dtValRecFed, dataAtual);
		
		return (isDtValRecFedVencida || vo.getDataValidadeRecFed() == null);
	}
	
	private boolean exibirDtValFgts(Long cgc, DatasVencimentosFornecedorVO vo) {
		Date dataAtual = DateUtil.truncaData(new Date());
		Date dtValFgts = vo.getDataValidadeFgts() != null ? DateUtil.truncaData(vo.getDataValidadeFgts()) : dataAtual;
		boolean isDtValFgtsVencida = DateUtil.validaDataMenor(dtValFgts, dataAtual);
		
		return (cgc != null && (isDtValFgtsVencida || vo.getDataValidadeFgts() == null));
	}
	
	public void inserirFornecedorIrregularFiscal(Integer fornNumero, DominioSituacaoEnvioEmail situacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoFornecedorIrregularFiscalId id = new ScoFornecedorIrregularFiscalId(fornNumero, scoFornecedorIrregularFiscalDAO
				.obterMaxNumeroScoFornecedorIrregularFiscal(fornNumero));
		ScoFornecedorIrregularFiscal fornIrregularFiscal = new ScoFornecedorIrregularFiscal();
		fornIrregularFiscal.setId(id);
		fornIrregularFiscal.setData(new Date());
		fornIrregularFiscal.setServidor(servidorLogado);
		fornIrregularFiscal.setSituacao(situacao);
		this.scoFornecedorIrregularFiscalDAO.persistir(fornIrregularFiscal);
	}
	
	public static String getCpfCnpjFormatado(ScoFornecedor item) {
		if (item.getCpf() == null) {
			if (item.getCgc() == null) {
				return StringUtils.EMPTY;
			}
			return CoreUtil.formatarCNPJ(item.getCgc());
		}
		return CoreUtil.formataCPF(item.getCpf());
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
