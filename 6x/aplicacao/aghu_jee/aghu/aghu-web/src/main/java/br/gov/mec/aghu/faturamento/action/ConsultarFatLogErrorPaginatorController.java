package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FtLogErrorVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ConsultarFatLogErrorPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 1724915921138936553L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private RelatorioResumoCobrancaAihController relatorioResumoCobrancaAihController;
	
	// Dados do Paciente
	private Integer pacCodigo;
	private String pacNome;
	private Integer pacProntuario;
	
	// FILTROS
	private Integer cthSeqSelected;
	private String origem;
	private Short ichSeqp;
	private String erro;
	private Integer phiSeqItem1;
	private Long codItemSus1;
	private DominioSituacaoMensagemLog situacao;

	private List<FtLogErrorVO> ftLogErrorVOs;
	
	private final String PageConsultarContaHospitalar = "consultarContaHospitalar";
	private final String PageEncerramentoPreviaConta = "faturamento-encerramentoPreviaConta";
	private final String PageManterContaHospitalar = "manterContaHospitalar";
	private final String PageInformarSolicitadoContaHospitalar = "faturamento-informarSolicitadoContaHospitalar";
	private final String PageConsultarFatError = "consultarFatLogError";
	private final String PageConsultarEspelhoAihList = "consultarEspelhoAIHList";
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	//relatorio imprime ESspelho
	private String fileName;
	
	public void limparPesquisa() {
		ichSeqp = null;
		erro = null;
		phiSeqItem1 = null;
		codItemSus1 = null;
		fileName ="";
		situacao = null;
		this.ftLogErrorVOs = null;// Estado original da tela é com a listagem ativa
		
        //reset estado da tabela
        UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":formConta:tabelaLogs");
        table.setValueExpression("sortBy", null);
	}

	public void pesquisar() {
		
		boolean administrarUnidadeFuncionalInternacao = getCascaFacade().usuarioTemPermissao(
				obterLoginUsuarioLogado(),"administrarUnidadeFuncionalInternacao");
		
		boolean leituraCadastrosBasicosFaturamento = getCascaFacade().usuarioTemPermissao(
				obterLoginUsuarioLogado(),"leituraCadastrosBasicosFaturamento");
		boolean manterCadastrosBasicosFaturamento = getCascaFacade().usuarioTemPermissao(
				obterLoginUsuarioLogado(),"manterCadastrosBasicosFaturamento");
		
		ftLogErrorVOs= faturamentoFacade.pesquisaFatLogErrorFatMensagensLog(getCthSeqSelected(), situacao, administrarUnidadeFuncionalInternacao,
										leituraCadastrosBasicosFaturamento, manterCadastrosBasicosFaturamento, ichSeqp, erro, phiSeqItem1, codItemSus1);
		
		Collections.sort(ftLogErrorVOs);
	}

	@Override
	public List<FtLogErrorVO> recuperarListaPaginada(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
			 
		List<FtLogErrorVO> listaFatLogErrors= new ArrayList<FtLogErrorVO>();
		
		boolean administrarUnidadeFuncionalInternacao = getCascaFacade().usuarioTemPermissao(
				obterLoginUsuarioLogado(),"administrarUnidadeFuncionalInternacao");
		
		boolean leituraCadastrosBasicosFaturamento = getCascaFacade().usuarioTemPermissao(
				obterLoginUsuarioLogado(),"leituraCadastrosBasicosFaturamento");
		boolean manterCadastrosBasicosFaturamento = getCascaFacade().usuarioTemPermissao(
				obterLoginUsuarioLogado(),"manterCadastrosBasicosFaturamento");
		
		listaFatLogErrors= faturamentoFacade.pesquisaFatLogErrorFatMensagensLog(getCthSeqSelected(), situacao, administrarUnidadeFuncionalInternacao,
										leituraCadastrosBasicosFaturamento, manterCadastrosBasicosFaturamento, ichSeqp, erro, phiSeqItem1, codItemSus1);
		return listaFatLogErrors;
	}

	
	
	@Override
	public Long recuperarCount() {
		boolean administrarUnidadeFuncionalInternacao = getCascaFacade().usuarioTemPermissao(
				obterLoginUsuarioLogado(),"administrarUnidadeFuncionalInternacao");
		
		boolean leituraCadastrosBasicosFaturamento = getCascaFacade().usuarioTemPermissao(
				obterLoginUsuarioLogado(),"leituraCadastrosBasicosFaturamento");
		boolean manterCadastrosBasicosFaturamento = getCascaFacade().usuarioTemPermissao(
				obterLoginUsuarioLogado(),"manterCadastrosBasicosFaturamento");
		
		return faturamentoFacade.pesquisaFatLogErrorFatMensagensLogCount(getCthSeqSelected(), situacao, administrarUnidadeFuncionalInternacao,
				leituraCadastrosBasicosFaturamento, manterCadastrosBasicosFaturamento, ichSeqp, erro, phiSeqItem1, codItemSus1);
	}
	
	public String visualizarFatError() {
		return PageConsultarFatError;
	}
	
	public String voltar(){
		limparPesquisa();
		if( ("contahospitalar").equalsIgnoreCase(origem)){
			return PageManterContaHospitalar;
		} else if( ("encerramentopreviaconta").equalsIgnoreCase(origem)){
			return PageEncerramentoPreviaConta;
		} else if( ("consultarContaHospitalar").equalsIgnoreCase(origem)){
			return PageConsultarContaHospitalar;
		} else if ("informarSolicitadoContaHospitalar".equalsIgnoreCase(origem)) {
			return PageInformarSolicitadoContaHospitalar;
		} else {
			return PageConsultarContaHospitalar;
		}
	}
	
	public String espelho() {
		return PageConsultarEspelhoAihList;
	}
	
	public void imprimirEspelho() {
		if (cthSeqSelected == null) {
			apresentarMsgNegocio(Severity.ERROR,"CONTA_NAO_SELECIONADA");
		} else {			
		    relatorioResumoCobrancaAihController.setCthSeq(cthSeqSelected);
		    relatorioResumoCobrancaAihController.setPacCodigo(pacCodigo);
		    relatorioResumoCobrancaAihController.setPacProntuario(pacProntuario);
		    relatorioResumoCobrancaAihController.setPacNome(pacNome);
		    relatorioResumoCobrancaAihController.definirValorPrevia();
		    relatorioResumoCobrancaAihController.criarArquivoPdf();
		    relatorioResumoCobrancaAihController.dispararDownload();
		}
	}
	
	
	public void dispararDownload(){
		if(!StringUtils.isEmpty(fileName)){
			try {
				this.download(fileName);
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_PDF, e, e.getLocalizedMessage()));
			}
		}
		fileName = null;
	}
	
	

	public Integer getCthSeqSelected() {
		return cthSeqSelected;
	}

	public void setCthSeqSelected(Integer cthSeqSelected) {
		this.cthSeqSelected = cthSeqSelected;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Short getIchSeqp() {
		return ichSeqp;
	}

	public void setIchSeqp(Short ichSeqp) {
		this.ichSeqp = ichSeqp;
	}

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public Integer getPhiSeqItem1() {
		return phiSeqItem1;
	}

	public void setPhiSeqItem1(Integer phiSeqItem1) {
		this.phiSeqItem1 = phiSeqItem1;
	}

	public Long getCodItemSus1() {
		return codItemSus1;
	}

	public void setCodItemSus1(Long codItemSus1) {
		this.codItemSus1 = codItemSus1;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public DominioSituacaoMensagemLog getSituacaoNaoEnc() {
		return DominioSituacaoMensagemLog.NAOENC;
	}

	public DominioSituacaoMensagemLog getSituacaoNaoCob() {
		return DominioSituacaoMensagemLog.NAOCOBR;
	}

	public DominioSituacaoMensagemLog getSituacaoNaoIncons() {
		return DominioSituacaoMensagemLog.INCONS;
	}

	public DominioSituacaoMensagemLog getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoMensagemLog situacao) {
		this.situacao = situacao;
	}
	
	public ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	public List<FtLogErrorVO> getFtLogErrorVOs() {
		return ftLogErrorVOs;
	}

	public void setFtLogErrorVOs(List<FtLogErrorVO> ftLogErrorVOs) {
		this.ftLogErrorVOs = ftLogErrorVOs;
	}
	
}