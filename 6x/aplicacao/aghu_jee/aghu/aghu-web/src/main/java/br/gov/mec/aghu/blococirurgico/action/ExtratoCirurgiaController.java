package br.gov.mec.aghu.blococirurgico.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ExtratoCirurgiaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	private static final long serialVersionUID = -7102047377830610820L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;

	private Integer crgSeq;
	private Boolean edicao;
    private Boolean exclusao;
	private String motivoCancelamento;
	private List<MbcExtratoCirurgia> extratosCirurgias;
	private MbcExtratoCirurgia extratoCirurgiaSelecionado;
	private Boolean dataNula;
	private Date criadoEm;

	public void inicializaParametros(Integer crgSeq) {
		edicao = cascaFacade.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "detalheRegistroDeCirurgiasAbaExtrato","editar");
		exclusao = cascaFacade.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "detalheRegistroDeCirurgiasAbaExtrato", "excluir");
        this.crgSeq = crgSeq;
		carregarListaExtratosCirurgias();
		carregaMotivoCancelamento();
		dataNula = true;
	}

	public void carregarListaExtratosCirurgias() {
		extratosCirurgias = blocoCirurgicoFacade.listarMbcExtratoCirurgiaPorCirurgia(crgSeq);
	}

	public void carregaMotivoCancelamento() {
		motivoCancelamento = null;
		MbcCirurgias cirurgia =  (extratosCirurgias != null && !extratosCirurgias.isEmpty() && extratosCirurgias.get(0) != null && extratosCirurgias.get(0).getCirurgia() != null) ? this.blocoCirurgicoFacade.obterCirurgiaPorSeq(extratosCirurgias.get(0).getCirurgia().getSeq()) : null;
		MbcMotivoCancelamento motivoCanc = (cirurgia !=null && cirurgia.getMotivoCancelamento() !=null) ? this.blocoCirurgicoFacade.obterMotivoCancelamentoPorChavePrimaria(cirurgia.getMotivoCancelamento().getSeq()): null;
		
		
		if (extratosCirurgias != null && !extratosCirurgias.isEmpty() && extratosCirurgias != null && !extratosCirurgias.isEmpty() && extratosCirurgias.get(0) != null && motivoCanc != null) {
			motivoCancelamento = motivoCanc.getDescricao();
		}
	}

	public void preparaEdicaoData(MbcExtratoCirurgia extratoCirurgiaSelecionado) {
		this.extratoCirurgiaSelecionado = extratoCirurgiaSelecionado;
		this.criadoEm = extratoCirurgiaSelecionado.getCriadoEm();
	}

	public String gravarData() {
		if (criadoEm == null) {
			apresentarMsgNegocio("dataExtratoCirurgia",Severity.ERROR,"MENSAGEM_EXTRATO_CIRURGIA_DATA_ENTRADA_OBRIGATORIA");
			dataNula = true;
			openDialog("modalEdicaoDataWG");
		} else {
			extratoCirurgiaSelecionado.setCriadoEm(criadoEm);
			dataNula = false;
			try {
				String mensagemSucesso = blocoCirurgicoFacade.persistirMbcExtratoCirurgia(extratoCirurgiaSelecionado);
				
				apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
				extratoCirurgiaSelecionado = null;
				carregarListaExtratosCirurgias();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}
	
	public void excluirData(MbcExtratoCirurgia extratoCirurgiaSelecionado){
		try {
			String mensagemSucesso = blocoCirurgicoFacade.excluirMbcExtratoCirurgia(extratoCirurgiaSelecionado, obterLoginUsuarioLogado());
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			extratoCirurgiaSelecionado = null;
			carregarListaExtratosCirurgias();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public List<MbcExtratoCirurgia> getExtratosCirurgias() {
		return extratosCirurgias;
	}

	public void setExtratosCirurgias(List<MbcExtratoCirurgia> extratosCirurgias) {
		this.extratosCirurgias = extratosCirurgias;
	}

	public MbcExtratoCirurgia getExtratoCirurgiaSelecionado() {
		return extratoCirurgiaSelecionado;
	}

	public void setExtratoCirurgiaSelecionado(MbcExtratoCirurgia extratoCirurgiaSelecionado) {
		this.extratoCirurgiaSelecionado = extratoCirurgiaSelecionado;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getDataNula() {
		return dataNula;
	}

	public void setDataNula(Boolean dataNula) {
		this.dataNula = dataNula;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Boolean getExclusao() {
		return exclusao;
	}

	public void setExclusao(Boolean exclusao) {
		this.exclusao = exclusao;
	}

}
