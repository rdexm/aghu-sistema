package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAltaDiagnosticos;
import br.gov.mec.aghu.model.MamAltaEvolucoes;
import br.gov.mec.aghu.model.MamAltaPrescricoes;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;

public class AltaAmbulatorialController extends ActionController {

	private static final long serialVersionUID = -7398622094194181425L;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private AipPacientes paciente;
	private Integer codigoPaciente;
	private Integer prontuario;
	private String voltarParaUrl;
	private List<AghAtendimentos> atendimentos;
	private AghAtendimentos atendimento;
	private String selectedTab;
	private Boolean showModalAtendimentos;
	private Integer seqCid;
	private Boolean cidEmEdicao;
	private MamAltaSumario altaSumario;
	private MamAltaDiagnosticos diagnostico;
	private MamAltaEvolucoes evolucao;
	private MamAltaPrescricoes prescricao;
	private List<MamAltaDiagnosticos> listDiagnosticos;
	private List<MamAltaPrescricoes> listPrescricoes;
	private Boolean prescricaoEmEdicao;
	private Boolean altaBloqueada;
	private String descricaoEvolucao;
	private Integer atdSeq;
	
	private static final String PAGE_PESQUISAR_ALTA_AMBULATORIAL = "ambulatorio-altaAmbulatorial";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private static final String PAGE_PESQUISAR_CID_CAPITULO_DIAGNOSTICO = "prescricaomedica-pesquisaCidCapituloDiagnostico";
	
	public void iniciar(){
	 

	 

		altaBloqueada = false;
		if(diagnostico == null){
			diagnostico = new MamAltaDiagnosticos();
		}
		if(prescricao == null){
			prescricao = new MamAltaPrescricoes();
		}
		if(cidEmEdicao == null){
			cidEmEdicao = false;
		}
		if(prescricaoEmEdicao == null){
			prescricaoEmEdicao = false;
		}
		
		if (codigoPaciente != null || prontuario != null) {
			if (codigoPaciente != null) {
				paciente = pacienteFacade.obterPacientePorCodigo(codigoPaciente);
			} else if (prontuario != null) {
				paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
			}
		}
		if(seqCid != null){
			diagnostico.setCid(aghuFacade.obterAghCidPorSeq(seqCid));
			seqCid = null;
		}else{
			setarCamposAtendimento();
		}
		if(atdSeq != null){
			atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
			paciente = atendimento.getPaciente();
			codigoPaciente = paciente.getCodigo();
			prontuario = paciente.getProntuario();
			preencherAlta();
		}
	
	}
	
	
	public void setarCamposAtendimento(){
		if (paciente != null) {
			codigoPaciente = paciente.getCodigo();
			prontuario = paciente.getProntuario();
			atendimentos = ambulatorioFacade.pesquisarAtendimentoParaAltaAmbulatorial(paciente.getCodigo(), null);
			if(atendimentos != null && !atendimentos.isEmpty()){
				if(atendimentos.size() == 1){
					atendimento = atendimentos.get(0);
					preencherAlta();
				}else{
					showModalAtendimentos = Boolean.TRUE;
				}
			}else{
				atendimento = null;
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_ALTA_AMBULATORIAL_NAO_POSSUI_ATENDIMENTO");
			}
		} else {
			limparCampos();
		}
	}
	
	public void preencherAlta(){
		altaSumario = ambulatorioFacade.pesquisarAltasSumariosPorNumeroConsulta(atendimento.getConsulta().getNumero());
		List<MamAltaEvolucoes> evolucoes = null;
		if(altaSumario != null){
			if(DominioIndPendenteDiagnosticos.V.equals(altaSumario.getPendente())){
				altaBloqueada = true;
			}else{
				altaBloqueada = false;
			}
			listDiagnosticos = ambulatorioFacade.pesquisarAltaDiagnosticosPorSumarioAlta(altaSumario.getSeq());
			listPrescricoes = ambulatorioFacade.procurarAltaPrescricoesBySumarioAltaEIndSelecionado(altaSumario, null);
			evolucoes = ambulatorioFacade.procurarAltaEvolucoesBySumarioAlta(altaSumario);
			if(evolucoes != null && !evolucoes.isEmpty()){
				evolucao = evolucoes.get(0);
				descricaoEvolucao = evolucao.getDescricao();
			}else{
				evolucao = new MamAltaEvolucoes();
			}
		}else{
			evolucao = new MamAltaEvolucoes();
		}
	}
	
	public String redirecionarPesquisaFonetica(){
		return PESQUISA_FONETICA;
	}
	
	public void limparCampos(){
		paciente = null;
		codigoPaciente = null;
		prontuario = null;
		atendimentos = null;
		atendimento = null;
		selectedTab = null;
		showModalAtendimentos = false;
		altaSumario = null;
		listDiagnosticos = null;
		seqCid = null;
		cidEmEdicao = false;
		prescricaoEmEdicao = false;
		descricaoEvolucao = null;
		altaSumario = null;
		diagnostico = new MamAltaDiagnosticos(); 
		prescricao = new MamAltaPrescricoes();
		altaBloqueada = false;
	}
	
	public String getTruncProntuarioNomePaciente(Long size){
		if(paciente != null){
			return StringUtil.trunc(paciente.getProntuario() + " - " + paciente.getNome(), Boolean.TRUE, size);
		}
		return "";
	}
	
	public void processarSelecaoAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
		preencherAlta();
		showModalAtendimentos = false;
	}
	
	public String voltar() {
		this.limparCampos();
		String retorno;
		if(voltarParaUrl != null){
			retorno = "voltar";
		}else{
			retorno = PAGE_PESQUISAR_ALTA_AMBULATORIAL;
		}
		return retorno;
	}
	
	public List<AghCid> pesquisarCids(String param) {
		List<AghCid> listaCids = null;
		try {
			listaCids = aghuFacade.pesquisarCidsSemSubCategoriaPorDescricaoOuIdOrdenadoPorDesc(param, 100);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return listaCids;
	}
	
	public String pesquisarCidCapitulo() {
		return PAGE_PESQUISAR_CID_CAPITULO_DIAGNOSTICO;
	}
	
	public void persistirItemPrescricao() {
		if(prescricao.getDescricao() != null){
			verificarAltaSumario();
			prescricao.setAltaSumario(altaSumario);
			ambulatorioFacade.persistirMamAltaPrescricoes(prescricao);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_ALTA_AMBULATORIAL_PRESCRICAO_ALTERADA");
			if(listPrescricoes == null){
				listPrescricoes = new ArrayList<MamAltaPrescricoes>();
			}
			if(!listPrescricoes.contains(prescricao)){
				listPrescricoes.add(prescricao);
			}	
			prescricao = new MamAltaPrescricoes();
			prescricaoEmEdicao = false;
		}else{
			 this.apresentarMsgNegocio("prescricao", Severity.ERROR, "CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_PRESCRICAO"));
		}
	}
	
	public void excluirItemPrescricao() {
		ambulatorioFacade.removerMamAltaPrescricoes(prescricao);
		this.apresentarMsgNegocio(Severity.INFO, "MSG_ALTA_AMBULATORIAL_PRESCRICAO_EXCLUIDA");
		listPrescricoes.remove(prescricao);
		prescricao = new MamAltaPrescricoes();
		prescricaoEmEdicao = false;
	}
	
	public void editarItemPrescricao(MamAltaPrescricoes prescricao) {
		this.prescricao = prescricao;
		prescricaoEmEdicao = true;
	}
	
	public void alterarIndSelecionadoPrescricao(MamAltaPrescricoes prescricao) {
		this.prescricao = prescricao;
		ambulatorioFacade.persistirMamAltaPrescricoes(prescricao);
		this.apresentarMsgNegocio(Severity.INFO, "MSG_ALTA_AMBULATORIAL_PRESCRICAO_ALTERADA", diagnostico.getDescricao());
	}
	
	
	public void cancelarEdicaoItemPrescricao() {
		prescricao = new MamAltaPrescricoes();
		prescricaoEmEdicao = false;
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			if (paciente != null) {
				setarCamposAtendimento();
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEdicaoEvolucao() {
		evolucao = null;
		descricaoEvolucao = null;
	}
	
	public void desbloquearAlta() {
		ambulatorioFacade.desbloquearMamAltaSumario(altaSumario);
		altaBloqueada = false;
	}
	
	public void gravarEvolucao() {
		if(descricaoEvolucao != null){
			verificarAltaSumario();
			evolucao.setDescricao(descricaoEvolucao);
			evolucao.setAltaSumario(altaSumario);
			ambulatorioFacade.persistirMamAltaEvolucoes(evolucao);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_ALTA_AMBULATORIAL_EVOLUCAO_ALTERADA");
		}else{
			this.apresentarMsgNegocio("evolucao", Severity.ERROR, "CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_EVOLUCAO"));
		}
	}
	
	public void verificarAltaSumario() {
		if(altaSumario == null){
			try {
				altaSumario = ambulatorioFacade.persistirMamAltaSumario(paciente, atendimento, obterLoginUsuarioLogado());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void persistirCid() {
		if(diagnostico.getCid() != null){
			try {
				verificarAltaSumario();
				diagnostico.setAltaSumario(altaSumario);
				ambulatorioFacade.persistirMamAltaDiagnosticos(diagnostico);
				this.apresentarMsgNegocio(Severity.INFO, "MSG_ALTA_AMBULATORIAL_CID_ALTERADO", diagnostico.getDescricao());
				if(listDiagnosticos == null){
					listDiagnosticos = new ArrayList<MamAltaDiagnosticos>();
				}
				if(!listDiagnosticos.contains(diagnostico)){
					listDiagnosticos.add(diagnostico);
				}	
				diagnostico = new MamAltaDiagnosticos();
				cidEmEdicao = false;
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}else{
			if(diagnostico.getCid() == null){
				this.apresentarMsgNegocio("cid", Severity.ERROR, "CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_CID"));
			}
		}
	}
	
	public void editarCid(MamAltaDiagnosticos diagnostico) {
		this.diagnostico = diagnostico;
		cidEmEdicao = true;
	}
	
	public void alterarIndSelecionadoCid(MamAltaDiagnosticos diagnostico) {
		this.diagnostico = diagnostico;
		try {
			ambulatorioFacade.persistirMamAltaDiagnosticos(diagnostico);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_ALTA_AMBULATORIAL_CID_ALTERADO", diagnostico.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEdicaoCid() {
		diagnostico = new MamAltaDiagnosticos();
		cidEmEdicao = false;
	}
	
	public void excluirCid() {
		ambulatorioFacade.removerMamAltaDiagnosticos(diagnostico);
		this.apresentarMsgNegocio(Severity.INFO, "MSG_ALTA_AMBULATORIAL_CID_EXCLUIDO", diagnostico.getDescricao());
		listDiagnosticos.remove(diagnostico);
		diagnostico = new MamAltaDiagnosticos();
		cidEmEdicao = false;
	}

	//Getters and Setters
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Boolean getShowModalAtendimentos() {
		return showModalAtendimentos;
	}

	public void setShowModalAtendimentos(Boolean showModalAtendimentos) {
		this.showModalAtendimentos = showModalAtendimentos;
	}

	public void setAtendimentos(List<AghAtendimentos> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public List<AghAtendimentos> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setCidEmEdicao(Boolean cidEmEdicao) {
		this.cidEmEdicao = cidEmEdicao;
	}

	public Boolean getCidEmEdicao() {
		return cidEmEdicao;
	}

	public void setAltaSumario(MamAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	public MamAltaSumario getAltaSumario() {
		return altaSumario;
	}

	public void setSeqCid(Integer seqCid) {
		this.seqCid = seqCid;
	}

	public Integer getSeqCid() {
		return seqCid;
	}

	public void setAltaBloqueada(Boolean altaBloqueada) {
		this.altaBloqueada = altaBloqueada;
	}

	public Boolean getAltaBloqueada() {
		return altaBloqueada;
	}

	public void setEvolucao(MamAltaEvolucoes evolucao) {
		this.evolucao = evolucao;
	}

	public MamAltaEvolucoes getEvolucao() {
		return evolucao;
	}

	public void setPrescricao(MamAltaPrescricoes prescricao) {
		this.prescricao = prescricao;
	}

	public MamAltaPrescricoes getPrescricao() {
		return prescricao;
	}

	public void setPrescricaoEmEdicao(Boolean prescricaoEmEdicao) {
		this.prescricaoEmEdicao = prescricaoEmEdicao;
	}

	public Boolean getPrescricaoEmEdicao() {
		return prescricaoEmEdicao;
	}

	public void setDiagnostico(MamAltaDiagnosticos diagnostico) {
		this.diagnostico = diagnostico;
	}

	public MamAltaDiagnosticos getDiagnostico() {
		return diagnostico;
	}

	public void setListDiagnosticos(List<MamAltaDiagnosticos> listDiagnosticos) {
		this.listDiagnosticos = listDiagnosticos;
	}

	public List<MamAltaDiagnosticos> getListDiagnosticos() {
		return listDiagnosticos;
	}

	public void setListPrescricoes(List<MamAltaPrescricoes> listPrescricoes) {
		this.listPrescricoes = listPrescricoes;
	}

	public List<MamAltaPrescricoes> getListPrescricoes() {
		return listPrescricoes;
	}

	public void setDescricaoEvolucao(String descricaoEvolucao) {
		this.descricaoEvolucao = descricaoEvolucao;
	}

	public String getDescricaoEvolucao() {
		return descricaoEvolucao;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}
}
