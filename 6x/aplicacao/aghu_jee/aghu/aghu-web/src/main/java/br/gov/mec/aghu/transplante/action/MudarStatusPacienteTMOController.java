package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.model.MtxMotivoAlteraSituacao;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.GerarExtratoListaTransplantesVO;
import br.gov.mec.aghu.transplante.vo.ListarTransplantesVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class MudarStatusPacienteTMOController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5836070893659916714L;
	
	private static final String BT_SELECTED = "bt-selected";
	private static final String REDIRECT_INCLUIR_PACIENTE_TMO = "transplante-incluirPacienteListaTransplanteTMOCRUD";
	private static final String REDIRECT_MUDAR_STATUS_PACIENTE_TMO = "transplante-mudarStatusPacienteTMO";
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private IncluirPacienteListaTransplanteTMOController incluirPacienteListaTransplanteTMOController;

	private String telaAnterior;
	
	private ListarTransplantesVO paciente;
	
	private String prontuario;
	
	private ListarTransplantesVO transplantesVO;
	
	private List<GerarExtratoListaTransplantesVO> listaExtratoAlteracoes;
	private GerarExtratoListaTransplantesVO selectModal;
	
	private MtxTransplantes transplante = new MtxTransplantes();
	
	private String selectedAguarDoador = null;
	private String selectedAguardTransplante = null;
	private String selectedTransplantado = null;
	private String selectedInativo = null;
	private String selectedStandBy = null;
	
	private Boolean btAguarDoador = Boolean.TRUE;
	private Boolean btAguardTransplante = Boolean.TRUE;
	private Boolean btTransplantado = Boolean.TRUE;
	private Boolean btInativo = Boolean.TRUE;
	private Boolean btStandBy = Boolean.TRUE;
	
	private DominioSituacaoTransplante situacao;
	
	private Integer masSeq;
	
	private List<MtxMotivoAlteraSituacao> listaMotivos = new ArrayList<MtxMotivoAlteraSituacao>();

	private Boolean pesquisar;

	private Boolean alterar;
	
	private static final String PAGE_REGISTRO_TRANSPLANTE_TMO = "transplante-registraTransplanteTMO";
	
	@Inject
	private RegistraTransplanteTMOController registraTransplanteTMOController;
	
	@PostConstruct
	public void inicializar() {
		this.begin(conversation,true);
	}
	
	public void iniciar(){	
		
		this.pesquisar = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "pesquisarListaPacientesTransplantesTmo", "pesquisar");
		this.alterar = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "alterarSituacaoTransplanteTmo", "alterar");
		
		if (this.transplantesVO != null && this.transplantesVO.getCodigoMtxTransplante() != null) {
			this.transplante = transplanteFacade.obterTransplanteEdicao(this.transplantesVO.getCodigoMtxTransplante());
			this.paciente = this.transplanteFacade.obterPacientePorCodTransplante(this.transplantesVO.getCodigoMtxTransplante());
			this.desabilitarBotoes(this.transplantesVO.getSituacaoTransplante());
			this.setarFocusBotaoSituacao(this.transplantesVO.getSituacaoTransplante());
			if (this.paciente.getProntuarioPaciente() != null) {
				this.prontuario = this.formatarMascaraProntuario(this.paciente.getProntuarioPaciente());
			} else {
				this.prontuario = null;
			}
		}
	}
	
	private String formatarMascaraProntuario(Integer prontuario) {
		
		String valor = prontuario.toString();
		int tamanho = valor.length();
		String mascara = "";
		
		for (int i = 0; i < tamanho; i++) {
			if (i == tamanho-1) {
				mascara = mascara.concat("-#"); // ULTIMO DIGITO
			} else {
				mascara = mascara.concat("#"); // DEMAIS DIGITOS
			}
		}
		
		return this.inserirMascara(valor, mascara);
	}	
	
	/**
     * Insere a máscara de formatação no valor da String informada.<br /><tt>Ex.: inserirMascara("11111111111",
     * "###.###.###-##")</tt>.
     * 
     * @param valor {@link String} que será manipulada.
     * @param mascara Máscara que será aplicada.
     * @return Valor com a máscara de formatação.
     */
    private String inserirMascara(String valor, String mascara) {

        String novoValor = "";
        int posicao = 0;

        for (int i = 0; mascara.length() > i; i++) {
            if (mascara.charAt(i) == '#') {
                if (valor.length() > posicao) {
                    novoValor = novoValor.concat(String.valueOf(valor.charAt(posicao)));
                    posicao++;
                } else {
                    break;
                }
            } else {
                if (valor.length() > posicao) {
                    novoValor = novoValor.concat(String.valueOf(mascara.charAt(i)));
                } else {
                    break;
                }
            }
        }
        return novoValor;
    }
	
	private void desabilitarBotoes(DominioSituacaoTransplante btSituacao){
		this.btAguarDoador = Boolean.TRUE;
		this.btAguardTransplante = Boolean.TRUE;
		this.btTransplantado = Boolean.TRUE;
		this.btInativo = Boolean.TRUE;
		this.btStandBy = Boolean.TRUE;
		
		if (!this.pesquisar && !this.alterar) {
			this.btAguarDoador = Boolean.FALSE;
			this.btAguardTransplante = Boolean.FALSE;
			this.btTransplantado = Boolean.FALSE;
			this.btInativo = Boolean.FALSE;
			this.btStandBy = Boolean.FALSE;
			return;
		}
		if (btSituacao.equals(DominioSituacaoTransplante.T) || btSituacao.equals(DominioSituacaoTransplante.A)) {
			this.btAguarDoador = Boolean.FALSE;
		}
		if (btSituacao.equals(DominioSituacaoTransplante.T) || btSituacao.equals(DominioSituacaoTransplante.E)) {
			this.btAguardTransplante = Boolean.FALSE;
		}
		if (!btSituacao.equals(DominioSituacaoTransplante.E)) {
			this.btTransplantado = Boolean.FALSE;
		}
		if (btSituacao.equals(DominioSituacaoTransplante.T) || btSituacao.equals(DominioSituacaoTransplante.I)) {
			this.btInativo = Boolean.FALSE;
		}
		if (btSituacao.equals(DominioSituacaoTransplante.T) || btSituacao.equals(DominioSituacaoTransplante.S)) {
			this.btStandBy = Boolean.FALSE;
		}
	}
	
	private void setarFocusBotaoSituacao(DominioSituacaoTransplante situacao){
		this.limparBotoes();
		if (situacao.equals(DominioSituacaoTransplante.A)) {
			this.selectedAguarDoador = BT_SELECTED;
		}
		else if (situacao.equals(DominioSituacaoTransplante.E)) {
			this.selectedAguardTransplante = BT_SELECTED;
		}
		else if (situacao.equals(DominioSituacaoTransplante.T)) {
			this.selectedTransplantado = BT_SELECTED;
		}
		else if (situacao.equals(DominioSituacaoTransplante.I)) {
			this.selectedInativo = BT_SELECTED;
		}
		else if (situacao.equals(DominioSituacaoTransplante.S)) {
			this.selectedStandBy = BT_SELECTED;
		}
	}
	
	private void limparBotoes() {
		this.selectedAguarDoador = null;
		this.selectedAguardTransplante = null;
		this.selectedTransplantado = null;
		this.selectedInativo = null;
		this.selectedStandBy = null;
	}
	
	public void alterarSituacao(String novaSituacao) throws ApplicationBusinessException{
		if (novaSituacao.equals("A")) {
			this.situacao = DominioSituacaoTransplante.A;
			this.abrirModalMotivo();
		}
		else if (novaSituacao.equals("E")) {
			this.situacao = DominioSituacaoTransplante.E;
			if (!this.transplantesVO.getSituacaoTransplante().equals(DominioSituacaoTransplante.A)) {
				this.abrirModalMotivo();
			}else{
				this.gravar();
			}
		}
		else if (novaSituacao.equals("T")) {
			this.situacao = DominioSituacaoTransplante.T;
			openDialog("modalConfirmacaoTransplantadoWG");
		}
		else if (novaSituacao.equals("I")) {
			this.situacao = DominioSituacaoTransplante.I;
			this.abrirModalMotivo();
		}
		else if (novaSituacao.equals("S")) {
			this.situacao = DominioSituacaoTransplante.S;
			this.abrirModalMotivo();
		}	
		else{
			this.situacao = null;
		}
	}
	
	public String gravar() throws ApplicationBusinessException{
		
		if (this.transplantesVO != null && this.transplantesVO.getCodigoMtxTransplante() != null && this.paciente != null) {
			boolean msg = this.transplanteFacade.mudarStatusPacienteTMO(this.transplantesVO.getCodigoMtxTransplante(), this.situacao, this.paciente.getCodigoPaciente(), this.masSeq);
			if (!msg) {
				apresentarMsgNegocio(Severity.INFO, "CONTA_FECHADA_TRANSPLANTE");
			}

			fecharModalMotivo();
			if (this.situacao != null && this.situacao.equals(DominioSituacaoTransplante.T)) {
				fecharModalConfirmacao();
			}
			this.desabilitarBotoes(this.situacao);
			this.setarFocusBotaoSituacao(this.situacao);
			this.masSeq = null;
			
			//#41785
			if(this.situacao.equals(DominioSituacaoTransplante.T)){
				registraTransplanteTMOController.setSeqTransplante(transplantesVO.getCodigoMtxTransplante());
				registraTransplanteTMOController.setNomePaciente(paciente.getNomePaciente());
				registraTransplanteTMOController.setProntuario(prontuario);
				registraTransplanteTMOController.setVoltaPara(telaAnterior);
				return PAGE_REGISTRO_TRANSPLANTE_TMO;
			}
		}
		return null;
	}
	
	
	public void abrirModalMotivo() throws ApplicationBusinessException{
		this.listaMotivos = this.transplanteFacade.listarMotivosAlteracaoSituacao();
		openDialog("modalMotivoAlteracaoSituacaoWG");
	}
	
	public void fecharModalMotivo(){
		closeDialog("modalMotivoAlteracaoSituacaoWG");
	}
	
	public void fecharModalConfirmacao(){
		closeDialog("modalConfirmacaoTransplantadoWG");
	}
	
	public String editarTransplante(){	
		
		if (paciente != null && paciente.getCodigoPaciente() != null) {
			incluirPacienteListaTransplanteTMOController.setPacCodigo(paciente.getCodigoPaciente());
		}
		if (transplante != null && transplante.getSeq() != null) {
			incluirPacienteListaTransplanteTMOController.setTransplante(transplante);
			incluirPacienteListaTransplanteTMOController.setAghCid(transplante.getCid());
			incluirPacienteListaTransplanteTMOController.setMtxOrigens(transplante.getOrigem());
			incluirPacienteListaTransplanteTMOController.setTipoTmo(transplante.getTipoTmo());
			
			if(transplante.getTipoTmo() != null && transplante.getTipoTmo() == DominioSituacaoTmo.G){
				incluirPacienteListaTransplanteTMOController.setTipoAlogenico(transplante.getTipoAlogenico());
			}
			incluirPacienteListaTransplanteTMOController.setStatusDoenca(transplante.getCriterioPriorizacao());
			incluirPacienteListaTransplanteTMOController.setDoador(transplante.getDoador());
			incluirPacienteListaTransplanteTMOController.setEditandoTransplante(true);
			incluirPacienteListaTransplanteTMOController.setTelaAnterior(REDIRECT_MUDAR_STATUS_PACIENTE_TMO);
		}
		return REDIRECT_INCLUIR_PACIENTE_TMO;
	}
	
	public void extratoTransplante(){	
		if(transplantesVO != null && transplantesVO.getCodigoMtxTransplante() != null){
			listaExtratoAlteracoes = transplanteFacade.consultarListagemExtratoTransplante(transplantesVO.getCodigoMtxTransplante());
			openDialog("modalGerarExtratoAlteracoesListaTransplantesTMOWG");
		}
	}
	
	public String voltar(){
		limpar();
		limparBotoes();
		return this.telaAnterior;
	}
	
	public void limpar(){
		this.paciente = null;
		this.transplantesVO = null;
		this.listaExtratoAlteracoes = null; 
		this.listaMotivos = null; 
		this.masSeq = null;
		this.selectModal = null;
		this.transplante = null;
		this.situacao = null;
	}

	public String getTelaAnterior() {
		return telaAnterior;
	}
	public void setTelaAnterior(String telaAnterior) {
		this.telaAnterior = telaAnterior;
	}
	public ListarTransplantesVO getPaciente() {
		return paciente;
	}
	public void setPaciente(ListarTransplantesVO paciente) {
		this.paciente = paciente;
	}
	public ListarTransplantesVO getTransplantesVO() {
		return transplantesVO;
	}
	public void setTransplantesVO(ListarTransplantesVO transplantesVO) {
		this.transplantesVO = transplantesVO;
	}
	public List<GerarExtratoListaTransplantesVO> getListaExtratoAlteracoes() {
		return listaExtratoAlteracoes;
	}
	public void setListaExtratoAlteracoes(List<GerarExtratoListaTransplantesVO> listaExtratoAlteracoes) {
		this.listaExtratoAlteracoes = listaExtratoAlteracoes;
	}
	public GerarExtratoListaTransplantesVO getSelectModal() {
		return selectModal;
	}
	public void setSelectModal(GerarExtratoListaTransplantesVO selectModal) {
		this.selectModal = selectModal;
	}
	public MtxTransplantes getTransplante() {
		return transplante;
	}
	public void setTransplante(MtxTransplantes transplante) {
		this.transplante = transplante;
	}
	public String getSelectedAguarDoador() {
		return selectedAguarDoador;
	}
	public void setSelectedAguarDoador(String selectedAguarDoador) {
		this.selectedAguarDoador = selectedAguarDoador;
	}
	public String getSelectedAguardTransplante() {
		return selectedAguardTransplante;
	}
	public void setSelectedAguardTransplante(String selectedAguardTransplante) {
		this.selectedAguardTransplante = selectedAguardTransplante;
	}
	public String getSelectedTransplantado() {
		return selectedTransplantado;
	}
	public void setSelectedTransplantado(String selectedTransplantado) {
		this.selectedTransplantado = selectedTransplantado;
	}
	public String getSelectedInativo() {
		return selectedInativo;
	}
	public void setSelectedInativo(String selectedInativo) {
		this.selectedInativo = selectedInativo;
	}
	public String getSelectedStandBy() {
		return selectedStandBy;
	}
	public void setSelectedStandBy(String selectedStandBy) {
		this.selectedStandBy = selectedStandBy;
	}
	public Boolean getBtAguarDoador() {
		return btAguarDoador;
	}
	public void setBtAguarDoador(Boolean btAguarDoador) {
		this.btAguarDoador = btAguarDoador;
	}
	public Boolean getBtAguardTransplante() {
		return btAguardTransplante;
	}
	public void setBtAguardTransplante(Boolean btAguardTransplante) {
		this.btAguardTransplante = btAguardTransplante;
	}
	public Boolean getBtTransplantado() {
		return btTransplantado;
	}
	public void setBtTransplantado(Boolean btTransplantado) {
		this.btTransplantado = btTransplantado;
	}
	public Boolean getBtInativo() {
		return btInativo;
	}
	public void setBtInativo(Boolean btInativo) {
		this.btInativo = btInativo;
	}
	public Boolean getBtStandBy() {
		return btStandBy;
	}
	public void setBtStandBy(Boolean btStandBy) {
		this.btStandBy = btStandBy;
	}
	public DominioSituacaoTransplante getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoTransplante situacao) {
		this.situacao = situacao;
	}
	public Integer getMasSeq() {
		return masSeq;
	}
	public void setMasSeq(Integer masSeq) {
		this.masSeq = masSeq;
	}
	public List<MtxMotivoAlteraSituacao> getListaMotivos() {
		return listaMotivos;
	}
	public void setListaMotivos(List<MtxMotivoAlteraSituacao> listaMotivos) {
		this.listaMotivos = listaMotivos;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

}
