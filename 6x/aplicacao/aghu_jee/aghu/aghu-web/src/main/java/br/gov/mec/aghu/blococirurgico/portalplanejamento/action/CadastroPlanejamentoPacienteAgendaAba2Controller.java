package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RegimeProcedimentoAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.TempoSalaAgendaVO;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class CadastroPlanejamentoPacienteAgendaAba2Controller extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 4254219625997598246L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private CadastroPlanejamentoPacienteAgendaController principalController;
	
	private VMbcProcEsp procedimento;

	private MbcAgendaProcedimento agendaProcedimento ;
	
	private List<MbcAgendaProcedimento> agendaProcedimentoList ;
	
	private Boolean modificouProcedimento;
	
	private final Integer maxRegitrosEsp = 100; 
	
	private List<MbcAgendaProcedimento> listaRemocao = new ArrayList<MbcAgendaProcedimento>();
	
	private MbcAgendaProcedimento itemSelecionado;
	
	private Boolean readOnlySuggestionOutrosProc = Boolean.FALSE;	
	private Boolean renderedColunaAcaoOutrosProc = Boolean.TRUE;
	
	@Inject
	private SecurityController securityController;
	
	public void inicio() {
		pesquisar();

		getValidarRegrasPermissao();
	}
	
	private void getValidarRegrasPermissao() {
		Boolean permissaoExecutar = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaOutrosProcExecutar", "cadastroPlanejamentoPacienteAgendaAbaOutrosProcExecutar");
		Boolean permissaoAlterar = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaOutrosProcAlterar", "cadastroPlanejamentoPacienteAgendaAbaOutrosProcAlterar");
		readOnlySuggestionOutrosProc = !permissaoExecutar && !permissaoAlterar;
		renderedColunaAcaoOutrosProc = permissaoExecutar || permissaoAlterar;
	}
	
	public List<VMbcProcEsp> pesquisarProcedimento(String objParam) throws ApplicationBusinessException {
		return this.returnSGWithCount(blocoCirurgicoPortalPlanejamentoFacade.pesquisarVMbcProcEspPorEsp(objParam, null, maxRegitrosEsp),pesquisarProcedimentoCount(objParam));
	}
	
	public Long pesquisarProcedimentoCount(String objParam) throws ApplicationBusinessException {
		return blocoCirurgicoPortalPlanejamentoFacade.pesquisarVMbcProcEspPorEspCount(objParam,null);
	}
	
	
	public void pesquisar() {
		listaRemocao = new ArrayList<MbcAgendaProcedimento>();
		modificouProcedimento=false;
		if(getAgdSeq() != null){
			agendaProcedimentoList = blocoCirurgicoPortalPlanejamentoFacade.pesquisarAgendaProcedimento(getAgdSeq());
		}
	}
	
	public void removerAgendaProcedimento(MbcAgendaProcedimento agendaProcedimento) {
		modificouProcedimento = true;
		for(int i = 0; i < agendaProcedimentoList.size(); i++) {
			if(agendaProcedimento.getProcedimentoCirurgico().getSeq().equals(agendaProcedimentoList.get(i).getProcedimentoCirurgico().getSeq())) {
				if(agendaProcedimento.getId() != null) {
					listaRemocao.add(agendaProcedimento);
				}
				getAgendaProcedimentoList().remove(i);
				break;
			}
		}
	}
	
	public String getTempoProcedimentoFormatado(VMbcProcEsp vMbcProcEsp) throws ParseException {
		StringBuffer tempo = new StringBuffer(vMbcProcEsp.getTempoMinimo().toString());
		while (tempo.length() < 4) {
			// Coloca zeros a esquerda
			tempo.insert(0, "0");
		}
		DateFormat formatacaoBanco = new SimpleDateFormat("HHmm");
		DateFormat formatacaoTela = new SimpleDateFormat("HH:mm");
		
		Date dataFormatadaBanco = formatacaoBanco.parse(tempo.toString());
		return formatacaoTela.format(dataFormatadaBanco);
	}
	
	
	public void adicionar() throws ParseException {
		TempoSalaAgendaVO tempoSala = null;
		RegimeProcedimentoAgendaVO regimeSus = null;
		modificouProcedimento = true;
		
		if(procedimento != null) {
			try {
				MbcAgendaProcedimento agendaProcedimento = new MbcAgendaProcedimento();
				agendaProcedimento.setQtde(Short.valueOf("1"));
				agendaProcedimento.setMbcAgendas(principalController.getAgenda());
				MbcEspecialidadeProcCirgsId id = new MbcEspecialidadeProcCirgsId(procedimento.getId().getPciSeq(),procedimento.getId().getEspSeq());
				MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs = blocoCirurgicoCadastroApoioFacade.obterEspecialidadeProcedimentoCirurgico(id);
				MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos = blocoCirurgicoCadastroApoioFacade.obterProcedimentoCirurgico(procedimento.getId().getPciSeq());
				agendaProcedimento.setProcedimentoCirurgico(mbcProcedimentoCirurgicos);
				agendaProcedimento.setMbcEspecialidadeProcCirgs(mbcEspecialidadeProcCirgs);
				if(getAgendaProcedimentoList() != null && getAgendaProcedimentoList().size() > 0){
					blocoCirurgicoFacade.validarAgendaProcedimentoAdicionadoExistente(getAgendaProcedimentoList(), agendaProcedimento);
				}else{
					this.setAgendaProcedimentoList(new ArrayList<MbcAgendaProcedimento>());
				}
				tempoSala = blocoCirurgicoFacade.validaTempoMinimo(getTempoSala(), procedimento);
				this.setTempoSala(tempoSala.getTempo());
				blocoCirurgicoFacade.validarQtdeAgendaProcedimento(agendaProcedimento);
				regimeSus = blocoCirurgicoFacade.populaRegimeSus(getDominioRegimeSus(), procedimento);
				this.setDominioRegimeSus(regimeSus.getRegime());
				
				getAgendaProcedimentoList().add(agendaProcedimento);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} finally {
				procedimento = null;
				
				if (tempoSala != null && tempoSala.getInfo() != null) {
					apresentarMsgNegocio(tempoSala.getInfo(), tempoSala.getMensagem(), tempoSala.getTempoSalaFormatada(), tempoSala.getDataFormatada(), tempoSala.getDescricaoProcedimento());
				}
				
				if (regimeSus != null && regimeSus.getSeveridade() != null) {
					apresentarMsgNegocio(regimeSus.getSeveridade(), regimeSus.getMensagem(), regimeSus.getDescricaoRegime(), regimeSus.getDescricaoRegimeProcedSus(), regimeSus.getDescricaoProc());
				}
			}
		}
	}
	
	public void limparParametros() {
		procedimento = null;
		agendaProcedimento = null;
		agendaProcedimentoList = null;
		listaRemocao = null;
		itemSelecionado = null;
	}

	public MbcAgendaProcedimento getAgendaProcedimento() {
		return agendaProcedimento;
	}

	public void setAgendaProcedimento(MbcAgendaProcedimento agendaProcedimento) {
		this.agendaProcedimento = agendaProcedimento;
	}

	public List<MbcAgendaProcedimento> getAgendaProcedimentoList() {
		return agendaProcedimentoList;
	}

	public void setAgendaProcedimentoList(
			List<MbcAgendaProcedimento> agendaProcedimentoList) {
		this.agendaProcedimentoList = agendaProcedimentoList;
	}

	public Integer getAgdSeq() {
		return principalController.getAgenda().getSeq();
	}
	
	public Short getSeqEspecialidade(){
		return principalController.getSeqEspecialidade();
	}

	public Boolean getModificouProcedimento() {
		return modificouProcedimento;
	}

	public void setModificouProcedimento(Boolean modificouProcedimento) {
		this.modificouProcedimento = modificouProcedimento;
	}
	
	public Date getTempoSala() {
		return principalController.getAgenda().getTempoSala();
	}
	
	public void setTempoSala(Date tempoSala) {
		principalController.getAgenda().setTempoSala(tempoSala);
	}
	
	public DominioRegimeProcedimentoCirurgicoSus getDominioRegimeSus(){
		return principalController.getAgenda().getRegime();
	}

	public void setDominioRegimeSus(DominioRegimeProcedimentoCirurgicoSus regimeSus){
		if (regimeSus != null) {
			principalController.getAgenda().setRegime(regimeSus);
		}
	}

	public List<MbcAgendaProcedimento> getListaRemocao() {
		return listaRemocao;
	}

	public void setListaRemocao(List<MbcAgendaProcedimento> listaRemocao) {
		this.listaRemocao = listaRemocao;
	}

	public Boolean getReadOnlySuggestionOutrosProc() {
		return readOnlySuggestionOutrosProc;
	}

	public void setReadOnlySuggestionOutrosProc(Boolean readOnlySuggestionOutrosProc) {
		this.readOnlySuggestionOutrosProc = readOnlySuggestionOutrosProc;
	}

	public Boolean getRenderedColunaAcaoOutrosProc() {
		return renderedColunaAcaoOutrosProc;
	}

	public void setRenderedColunaAcaoOutrosProc(Boolean renderedColunaAcaoOutrosProc) {
		this.renderedColunaAcaoOutrosProc = renderedColunaAcaoOutrosProc;
	}
	
	public VMbcProcEsp getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(VMbcProcEsp procedimento) {
		this.procedimento = procedimento;
	}
	
	public void setItemSelecionado(MbcAgendaProcedimento itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	
	public MbcAgendaProcedimento getItemSelecionado() {
		return this.itemSelecionado;
	}
}
