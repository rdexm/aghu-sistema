package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.model.MtxMotivoAlteraSituacao;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.GerarExtratoListaTransplantesVO;
import br.gov.mec.aghu.transplante.vo.PacienteAguardandoTransplanteOrgaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class MudarStatusPacienteRinsController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7849791957684267956L;
	private static final String BT_SELECTED = "bt-selected";
	private static final String REDIRECT_INCLUIR_PACIENTE = "transplante-incluirPacienteListaTransplanteCRUD";
	private static final String REDIRECT_MUDAR_STATUS_PACIENTE_RINS = "transplante-mudarStatusPacienteRins";
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private IncluirPacienteListaTransplanteController incluirPacienteListaTransplanteController;

	private String telaAnterior;
	
	private PacienteAguardandoTransplanteOrgaoVO paciente;
	
	private Integer pacCodigo;
	
	private String prontuario;
	
	private Integer seqTransplante;
	
	private List<GerarExtratoListaTransplantesVO> listaExtratoAlteracoes;
	private GerarExtratoListaTransplantesVO selectModal;
	
	private MtxTransplantes transplante = new MtxTransplantes();
	
	private String selectedAguardTransplante = null;
	private String selectedTransplantado = null;
	private String selectedInativo = null;
	private String selectedRetidadoLista = null;
	
	private Boolean btAguardTransplante = Boolean.TRUE;
	private Boolean btTransplantado = Boolean.TRUE;
	private Boolean btInativo = Boolean.TRUE;
	private Boolean btRetidadoLista = Boolean.TRUE;
	
	private DominioSituacaoTransplante situacao;
	
	private Integer masSeq;
	
	private List<MtxMotivoAlteraSituacao> listaMotivos = new ArrayList<MtxMotivoAlteraSituacao>();

	private Boolean pesquisar;

	private Boolean alterar;
	
	@PostConstruct
	public void inicializar() {
		this.begin(conversation,true);
	}
	
	public void iniciar(){	
		
		this.pesquisar = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "pesquisarPacienteListaTransplanteOrgaos", "pesquisar");
		this.alterar = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "alterarSituacaoTransplanteOrgaos", "alterar");
		
		if (this.seqTransplante != null) {
			this.transplante = transplanteFacade.obterTransplanteEdicao(this.seqTransplante);
			this.paciente = this.transplanteFacade.obterPacientePorCodTransplanteRins(this.seqTransplante);
			this.desabilitarBotoes(this.transplante.getSituacao());
			this.setarFocusBotaoSituacao(this.transplante.getSituacao());
			if (this.paciente.getPacProntuario() != null) {
				this.prontuario = this.formatarMascaraProntuario(this.paciente.getPacProntuario());
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
		this.btAguardTransplante = Boolean.TRUE;
		this.btTransplantado = Boolean.TRUE;
		this.btInativo = Boolean.TRUE;
		this.btRetidadoLista = Boolean.TRUE;
		
		if (!this.pesquisar && !this.alterar) {
			this.btAguardTransplante = Boolean.FALSE;
			this.btTransplantado = Boolean.FALSE;
			this.btInativo = Boolean.FALSE;
			this.btRetidadoLista = Boolean.FALSE;
			return;
		}
		if (!btSituacao.equals(DominioSituacaoTransplante.I)) {
			this.btAguardTransplante = Boolean.FALSE;
		}
		if (!btSituacao.equals(DominioSituacaoTransplante.E)) {
			this.btTransplantado = Boolean.FALSE;
		}
		if (!btSituacao.equals(DominioSituacaoTransplante.E)) {
			this.btInativo = Boolean.FALSE;
		}
		if (btSituacao.equals(DominioSituacaoTransplante.E) || btSituacao.equals(DominioSituacaoTransplante.I)) {
			this.btRetidadoLista = Boolean.TRUE;
		}else{
			this.btRetidadoLista = Boolean.FALSE;
		}
	}
	
	private void setarFocusBotaoSituacao(DominioSituacaoTransplante situacao){
		this.limparBotoes();
		if (situacao.equals(DominioSituacaoTransplante.E)) {
			this.selectedAguardTransplante = BT_SELECTED;
		}
		else if (situacao.equals(DominioSituacaoTransplante.T)) {
			this.selectedTransplantado = BT_SELECTED;
		}
		else if (situacao.equals(DominioSituacaoTransplante.I)) {
			this.selectedInativo = BT_SELECTED;
		}
		else if (situacao.equals(DominioSituacaoTransplante.R)) {
			this.selectedRetidadoLista  = BT_SELECTED;
		}
	}
	
	private void limparBotoes() {
		this.selectedAguardTransplante = null;
		this.selectedTransplantado = null;
		this.selectedInativo = null;
		this.selectedRetidadoLista = null;
	}
	
	public String editarTransplante(){	
		
		if (this.pacCodigo != null) {
			
			incluirPacienteListaTransplanteController.setPacCodigo(this.pacCodigo);
			incluirPacienteListaTransplanteController.setSeqTransplante(this.seqTransplante);
			incluirPacienteListaTransplanteController.setTelaAnterior(REDIRECT_MUDAR_STATUS_PACIENTE_RINS);
		}
		return REDIRECT_INCLUIR_PACIENTE; 
	}
	
	public void extratoTransplante(){	
		if(this.seqTransplante != null){
			listaExtratoAlteracoes = transplanteFacade.consultarListagemExtratoTransplante(this.seqTransplante);
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
		this.seqTransplante = null;
		this.listaExtratoAlteracoes = null; 
		this.listaMotivos = null; 
		this.masSeq = null;
		this.selectModal = null;
		this.transplante = null;
		this.situacao = null;
	}
	
	public void alterarSituacao(String novaSituacao) throws ApplicationBusinessException{
		if (novaSituacao.equals("E")) {
			this.situacao = DominioSituacaoTransplante.E;
			this.abrirModalMotivo();
		}
		else if (novaSituacao.equals("T")) {
			this.situacao = DominioSituacaoTransplante.T;
			openDialog("modalConfirmacaoTransplantadoWG");
		}
		else if (novaSituacao.equals("I")) {
			this.situacao = DominioSituacaoTransplante.I;
			this.abrirModalMotivo();
		}
		else if (novaSituacao.equals("R")) {
			this.situacao = DominioSituacaoTransplante.R;
			this.abrirModalMotivo();
		}	
		else{
			this.situacao = null;
		}
	}
	
	public void gravar() throws ApplicationBusinessException{
		
		if (this.seqTransplante != null) {
			this.transplanteFacade.mudarStatusPacienteRins(this.seqTransplante, this.situacao, this.masSeq);

			if (this.situacao != null && this.situacao.equals(DominioSituacaoTransplante.T)) {
				fecharModalConfirmacao();
			}else{
				fecharModalMotivo();
			}
			this.desabilitarBotoes(this.situacao);
			this.setarFocusBotaoSituacao(this.situacao);
			this.masSeq = null;
		}
		
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
	
	public String getTelaAnterior() {
		return telaAnterior;
	}
	public void setTelaAnterior(String telaAnterior) {
		this.telaAnterior = telaAnterior;
	}
	public PacienteAguardandoTransplanteOrgaoVO getPaciente() {
		return paciente;
	}
	public void setPaciente(PacienteAguardandoTransplanteOrgaoVO paciente) {
		this.paciente = paciente;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public Integer getSeqTransplante() {
		return seqTransplante;
	}
	public void setSeqTransplante(Integer seqTransplante) {
		this.seqTransplante = seqTransplante;
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
	public String getSelectedRetidadoLista() {
		return selectedRetidadoLista;
	}
	public void setSelectedRetidadoLista(String selectedRetidadoLista) {
		this.selectedRetidadoLista = selectedRetidadoLista;
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
	public Boolean getBtRetidadoLista() {
		return btRetidadoLista;
	}
	public void setBtRetidadoLista(Boolean btRetidadoLista) {
		this.btRetidadoLista = btRetidadoLista;
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

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
}
