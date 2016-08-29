package br.gov.mec.aghu.compras.autfornecimento.action;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaAutorizacaoFornecimentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFPVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.AutorizacaoFornPedidosVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfEmpenho;
import br.gov.mec.aghu.model.ScoContaCorrenteFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.mail.AnexoEmail;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class EnvioEmailAssinaturaAFController extends ActionReport{

	private static final Log LOG = LogFactory.getLog(EnvioEmailAssinaturaAFController.class);
	
	private static final long serialVersionUID = -8005769024181822851L;
	
	@EJB
	protected IParametroFacade parametroFacade;
		
	@EJB
	protected IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
		
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	
	String fornSemContato = "";
	private List<RelatorioAFPVO> dadosAfp = new ArrayList<RelatorioAFPVO>();	
	
	@Inject
	private EmailUtil emailUtil;
	
	@Inject
	private RelatorioPacientesCUMController relatorioPacientesCUMController;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public List<String> buscarContatosFornecedor(Integer lctNumero , short nroComplemento, ScoFornecedor fornecedor) throws BaseException{
		
		List<String> listaContatos =   autFornecimentoFacade.obterContatosPorEmailFornecedor(fornecedor);
		
		if (listaContatos!=null && listaContatos.size()>0){
			return listaContatos;
		}
		else {
			if (fornSemContato!=null && fornSemContato.length()> 0){ fornSemContato = fornSemContato.concat(" ,"); 
			
			}
			fornSemContato = fornSemContato.concat(" "+lctNumero+ " / "+nroComplemento+ " ");
		
			
		}
		return null;
	}
	
	
	public void enviarEmail(List<PesquisaAutorizacaoFornecimentoVO> listaEnvioAfps, List<AutorizacaoFornPedidosVO> listaSelecionados) throws IOException, BaseException, JRException, SystemException, DocumentException {
		   fornSemContato = null;
		   if (listaEnvioAfps!=null && listaEnvioAfps.size()>0){
				for (PesquisaAutorizacaoFornecimentoVO afVO: listaEnvioAfps){
					if (afVO.getNumeroAFP()!=null){					
						this.enviarPorLista(afVO.getNumeroAFP(),  afVO.getLctNumero(),  afVO.getNumeroComplemento(),
																			afVO.getAfnNumero(), afVO.getPropostaFornecedor(), afVO.getFornecedor());
					}
				  }
			}
			else if (listaSelecionados!=null && listaSelecionados.size()>0){
				for (AutorizacaoFornPedidosVO afVO: listaSelecionados){
					this.dadosAfp = new ArrayList<RelatorioAFPVO>();				
					this.enviarPorLista(afVO.getNumeroAFP(),  afVO.getLctNumero(),  afVO.getNumeroComplemento(),
																	afVO.getAfnNumero(), afVO.getScoPropostaFornecedor(), afVO.getFornecedor());
					
				}
				
			}			
		
		this.verificarAfsNaoEnviadas();
	}
	

	public void enviarPorLista(Integer numeroAfp, Integer lctNumero, Short nroCompl, Integer afnNumero, 
									ScoPropostaFornecedor proposta, ScoFornecedor pFornecedor)
									throws IOException, BaseException, JRException, SystemException, DocumentException{
		
		AghParametros razaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		
		
		List<String> destinatarios = buscarContatosFornecedor(lctNumero, nroCompl, pFornecedor);
		
		if (destinatarios!=null && destinatarios.size()>0){
		
			this.popularDados(numeroAfp, lctNumero, nroCompl);
	
			DocumentoJasper documento = gerarDocumento();
			AnexoEmail anexoEmail = new AnexoEmail(this.buscarMensagem("NOME_ARQUIVO_EMAIL")+numeroAfp+".pdf",
					documento.getPdfByteArray(false) , "application/pdf");
								
			AnexoEmail anexoEmailCum = null;
			
			if (comprasFacade.contarProgrEntregasPorAfeAfeNum(afnNumero, numeroAfp) > 0){
				relatorioPacientesCUMController.setAfeAfnNumero(afnNumero);
				relatorioPacientesCUMController.setAfeNumero(numeroAfp);
				DocumentoJasper documentoCum = relatorioPacientesCUMController.gerarDocumentoEmail();
				if (documentoCum != null){
					anexoEmailCum = new AnexoEmail("PAC_AFP_"+afnNumero + "_" + numeroAfp +".pdf",
							documentoCum.getPdfByteArray(false) , "application/pdf");
				}			
			}
			
			String remetente = this.obterRemetente();
			String assunto = razaoSocial.getVlrTexto()+" "+this.obterAssunto(lctNumero+"/"+nroCompl);
			String conteudo = this.obterConteudo(razaoSocial.getVlrTexto());
			
			try {	
				if(anexoEmailCum != null){
				   emailUtil.enviaEmailHtml(remetente, destinatarios, null, assunto, conteudo, anexoEmail, anexoEmailCum);
				} else {
				   emailUtil.enviaEmailHtml(remetente, destinatarios, null, assunto, conteudo, anexoEmail);	
				}
			
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_ENVIAR_EMAIL");
			}
			
			 
			ScoPropostaFornecedor propostaFornecedor = this.pacFacade.obterPropostaFornecedor(new ScoPropostaFornecedorId(lctNumero,pFornecedor.getNumero()));
			// updates 
			this.updateParcelasAFP(afnNumero, numeroAfp, propostaFornecedor.getPrazoEntrega());
			this.updateAFP(afnNumero, numeroAfp);
			
			this.apresentarMsgNegocio(Severity.INFO, "ENVIO_EMAIL_SUCESSO");
		}	
	}	
	
	public void updateParcelasAFP(Integer numAf, Integer numAfp, Short prazoEntrega) throws ApplicationBusinessException{
		this.autFornecimentoFacade.updateParcelasAFP(numAf, numAfp, prazoEntrega);
	}
	
	public void updateAFP(Integer numAf, Integer numAfp) throws ApplicationBusinessException{
		this.autFornecimentoFacade.updateAFP(numAf, numAfp);
	}
	
	public void verificarAfsNaoEnviadas(){
		
		if (fornSemContato!=null && fornSemContato.length()>0){
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_FORN_DESABILITADO_REC_EMAIL", fornSemContato);
		}
		
	}
	
	public String obterRemetente() throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		AghParametros dominio = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DOMINIO_EMAIL);
		String remetente = servidorLogado.getEmail();
		
		if (remetente==null || "".equalsIgnoreCase(remetente)){
			remetente = servidorLogado.getUsuario()+dominio.getVlrTexto();
		}
		return remetente;
	}
	
	
	public String obterAssunto(String afnNumeroCompl) throws ApplicationBusinessException{
		
		return this.buscarMensagem("MENSAGEM_ASSUNTO_EMAIL")+afnNumeroCompl;
	}
	
	
	public String obterConteudo(String razaoSocial) throws ApplicationBusinessException{
		AghParametros matriculaCC = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_MATR_CHEFE_CPRAS);
		AghParametros vinculoCC = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_VIN_CHEFE_CPRAS);
		
		this.registroColaboradorFacade.obterNomePessoaServidor(vinculoCC.getVlrNumerico().intValue(), 
																matriculaCC.getVlrNumerico().intValue());
		return this.buscarMensagem("MENSAGEM_CONTEUDO_EMAIL", razaoSocial, 
				this.registroColaboradorFacade.obterNomePessoaServidor(vinculoCC.getVlrNumerico().intValue(), 
						matriculaCC.getVlrNumerico().intValue()));
	}
	
	
	protected String buscarMensagem(String chave, Object... parametros) {
		String mensagem = this.getBundle().getString(chave);
		mensagem = MessageFormat.format(mensagem, parametros);
		return mensagem;
	}
	
	
	public void popularDados(Integer afpNumero, Integer numPac, Short nroComplemento ) throws BaseException, JRException, SystemException, IOException {
		
		AghParametros pEspecieEmpenhoOrig = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ESPECIE_EMPENHO_ORIG);
		RelatorioAFPVO afp = this.autFornecimentoFacade.pesquisarAFPedidoPorNumEPac(afpNumero, numPac, nroComplemento , pEspecieEmpenhoOrig.getVlrNumerico().intValue());
		
		
		this.dadosAfp.add(afp);
	//	for (ScoAutorizacaoFornecedorPedido pedido:  afp){
	//		this.autFornecimentoFacade.refresh(pedido);
	//	}
	
	}

	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		try {
			
			AghParametros cnpj = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_CGC);
			AghParametros razaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			AghParametros endereco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_LOGRADOURO);
			AghParametros cep = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CEP);
			AghParametros localEntrega = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_LOCAL_ENTREGA);
			AghParametros endEntrega = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_ENDERECO_ENTREGA);
			AghParametros horEntrega = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_HORARIO_ENTREGA);
			String caminhoLogoHosp = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_RELATIVO_JEE7);
			String caminhoLogoUfrgs = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_UNIVERSIDADE_RELATIVO_JEE7);
			AghParametros dddFoneHospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DDD_FONE_HOSPITAL);
			AghParametros prefixoHospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PREFIXO_FONE_HOSPITAL);
			AghParametros pAfpAviso = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AFP_AVISO);
			
			params.put("cnpj", cnpj.getVlrTexto());
			params.put("razaoSocial", razaoSocial.getVlrTexto());
			params.put("endereco", endereco.getVlrTexto());
			params.put("cep", cep.getVlrTexto());
			params.put("caminhoLogoHosp", caminhoLogoHosp);
			params.put("localEntrega", localEntrega.getVlrTexto());
			params.put("endEntrega", endEntrega.getVlrTexto());
			params.put("horEntrega", horEntrega.getVlrTexto());
			params.put("caminhoLogoUfrgs", caminhoLogoUfrgs);
			params.put("dddFoneHospital", dddFoneHospital.getVlrNumerico());
			params.put("prefixoHospital", prefixoHospital.getVlrNumerico());
			params.put("previsaoEntrega", this.getPrevisaoEntregaParametro());
			params.put("pAfpAviso", pAfpAviso.getVlrTexto());
			
			
			params.putAll(buscarNumeroListaEmpenho());
			params.putAll(this.buscarParametrosAutFornecimentoPedido());
		
			params.put("SUBREPORT_DIR", "br/gov/mec/aghu/compras/report/");
				
			
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		
		 catch (BaseException e) {
			 LOG.error(e.getMessage(), e);
		}

		return params;

	}
	
	public Map<String, Object>  buscarParametrosAutFornecimentoPedido() throws BaseException{
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		try {
			String prazoParcelas = this.autFornecimentoFacade.buscarPrazosAtendimento(this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getCondicaoPagamentoPropos().getNumero());
			if (prazoParcelas!=null){
				params.put("prazoParcelas", prazoParcelas+ " dias");
			}	
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		
		if (this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getPropostaFornecedor()!=null && 
				this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor()!=null
				&& this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor().getContaCorrenteFornecedor()!=null){
		
			params.putAll(this.buscaParametrosContaCorrente(this.comprasFacade.obterScoContaCorrenteFornecedorPorFornecedor(this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor())));
	}
		
		return params;
	}
	
	
	public Map<String, Object> buscarNumeroListaEmpenho() throws BaseException{
		Map<String, Object> params = new HashMap<String, Object>();
		Integer numAf = this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getNumero();
		AghParametros pEspecieEmpenho = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ESPECIE_EMPENHO_ORIG);
				
		Integer numSeqAf = this.autFornecimentoFacade.obterMaxNumEmpenhoPorAfeEspecie(numAf, pEspecieEmpenho.getVlrNumerico().intValue());
		
		if (numSeqAf!=null){
			ScoAfEmpenho afEmpenho = this.autFornecimentoFacade.obterEmpenhoPorChavePrimaria(numSeqAf);
			String numeroEmpenho = (afEmpenho!=null && afEmpenho.getAnoEmpenho()!=null?afEmpenho.getAnoEmpenho():"")+ "/ "+afEmpenho.getNumero();
			params.put("numeroEmpenho", numeroEmpenho);
			
			String listaEmpenho = (afEmpenho!=null && afEmpenho.getAnoListaItensAfSiafi()!=null?
								afEmpenho.getAnoListaItensAfSiafi():"")+ "/ "+ (afEmpenho!=null && afEmpenho.getNroListaItensAfSiafi()!=null?
								afEmpenho.getNroListaItensAfSiafi():"");
			params.put("listaEmpenho", listaEmpenho);
		}

		return params;
		
	}
	
	public Date getPrevisaoEntregaParametro(){
		
		GregorianCalendar gcal = new GregorianCalendar();  
		gcal.setTime(new Date());
		gcal.add(Calendar.DAY_OF_MONTH, 5);
				
		return  gcal.getTime();
	}
	
	
	public Map<String, Object> buscaParametrosContaCorrente(List<ScoContaCorrenteFornecedor> listaCcForncecedor){
		 
		 Map<String, Object> params = new HashMap<String, Object>();
		 
		 for(ScoContaCorrenteFornecedor ccForn: listaCcForncecedor){
			 if (ccForn.getIndPreferencial()== DominioSimNao.S){
				 params.put("bcoCodigo", ccForn.getId().getAgbBcoCodigo().toString());
				 params.put("agCodigo", ccForn.getId().getAgbCodigo().toString());
				 params.put("contaCorrente", ccForn.getId().getContaCorrente());
			 }
			 
		 }
		 
		 return params;
	 }
	
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		if (getDadosAfp()!=null){
			return getDadosAfp();
		}
		else { return null;	}
	}


	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/AutorizacaoFornecimentoPedido.jasper";
		
	}


	public List<RelatorioAFPVO> getDadosAfp() {
		return dadosAfp;
	}


	public void setDadosAfp(List<RelatorioAFPVO> dadosAfp) {
		this.dadosAfp = dadosAfp;
	}


	 

}
