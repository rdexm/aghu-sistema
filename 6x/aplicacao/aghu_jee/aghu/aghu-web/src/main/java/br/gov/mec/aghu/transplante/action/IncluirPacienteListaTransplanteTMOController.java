package br.gov.mec.aghu.transplante.action;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioFatorRh;
import br.gov.mec.aghu.dominio.DominioGrupoSanguineo;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.model.MtxOrigens;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class IncluirPacienteListaTransplanteTMOController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1373845286579069436L;
	
	private static final String PAGE_CID_POR_CAPITULO = "internacao-pesquisaCid";
	private static final String PAGE_PESQUISA_PACIENTE = "paciente-pesquisaPaciente";
	private static final String MENSAGEM_SUCESSO_INCLUSAO = "MENSAGEM_SUCESSO_INCLUSAO_TRANSPLANTE_TMO";
	private static final String MENSAGEM_SUCESSO_EDICAO = "MENSAGEM_SUCESSO_EDICAO_TRANSPLANTE_TMO";
	private static final String PAGE_PACIENTES_LISTAS_TMO ="transplante-pacientesListaTMO" ; //50184
	
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	private Integer pacCodigo; 
	private String telaAnterior;
	private String prontuario;
	
	private AipPacientes paciente = new AipPacientes();
	private AipPacientes doador = new AipPacientes();
	private AipRegSanguineos regSanguineo = new AipRegSanguineos();
	private MtxTransplantes transplante = new MtxTransplantes();
	
	
	private String idadeIngresso;
	private Integer escore;
	private String doadorFormatado;
	private DominioSituacaoTmo tipoTmo;
	
	private AghCid aghCid;
	private MtxOrigens mtxOrigens;
	private DominioGrupoSanguineo grupoSanguineo;
	private DominioFatorRh fatorRh;
	private boolean editandoTransplante = false;
	private Boolean modalConfirmacao1 = Boolean.FALSE;
	private Boolean modalConfirmacao2 = Boolean.FALSE;
	
	//46495
	private MtxCriterioPriorizacaoTmo statusDoenca;
	private List<MtxCriterioPriorizacaoTmo> listaStatusDoenca;
	private DominioTipoAlogenico tipoAlogenico;
	private boolean desabilitaCodDoador = false;
	
	//Paramentros da tela Anterior
	@Inject
	private PesquisaPacienteController pesquisaPacienteController; 
	
	private AipPacientes pacienteRetorno;
	private Boolean respeitarOrdemRetorno;
	private Boolean realizouPesquisaFoneticaRetorno;
	
	private Boolean habilitaGrupoSanguinio;
	private Boolean habilitaFator;
	private boolean vindoDaTelaCIDCapitulo;
	
	private enum IncluirPacienteListaTransplanteTMOControllerExceptionCode implements BusinessExceptionCode {
		ERRO_INGRESSO_MENOR_NASCIMENTO,ERRO_DIAGNOSTICO_MENOR_NASCIMENTO;
	}

	@PostConstruct
	public void inicializar() {
		this.begin(conversation,true);
	}
	
	public void iniciar(){
		if(vindoDaTelaCIDCapitulo){
			vindoDaTelaCIDCapitulo = false;
			return;
		}
		if(this.pacCodigo != null){
			this.paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo);
			if(this.paciente != null && this.paciente.getDtNascimento() != null){
				this.calcularIdadeIngresso();
				if (this.paciente.getProntuario() != null) {
					this.prontuario = this.formatarMascaraProntuario(this.paciente.getProntuario());
				} else {
					this.prontuario = null;
				}
			}
			inicializarRegSanguineo(this.pacCodigo);
		}
		
		if (this.doador != null && this.doador.getProntuario() != null && this.doador.getNome() != null) {
			this.doadorFormatado = this.formatarMascaraProntuario(this.doador.getProntuario()) + " - " + this.doador.getNome();
		}
		setDesabilitaCodDoador(false);
		if(this.editandoTransplante){
			carregarStatusDoencaPaciente();
		}
	}
	
	/**
	 * Carrega os dados do Registro Sanguineo do paciente ao acessar a pagina.
	 *  
	 * @param pacCodigo {@link Integer}
	 */
	private void inicializarRegSanguineo(Integer pacCodigo) {
		habilitaGrupoSanguinio = true;
		habilitaFator = true;
		this.regSanguineo = this.pacienteFacade.obterRegSanguineoPorCodigoPaciente(pacCodigo); 
		if (this.regSanguineo != null) {
			
			if(this.regSanguineo.getGrupoSanguineo() != null){
				this.grupoSanguineo = this.obterGrupoSanguineoPorCodigo(this.regSanguineo.getGrupoSanguineo());
			}else{
				popularGrupoSanguineoExamesRealizados();
			}
			
			if(this.regSanguineo.getFatorRh() != null){
				this.fatorRh = this.obterFatorRhPorCodigo(this.regSanguineo.getFatorRh());
			}else{
				popularFatorRhExamesRealizados();
			}
			
		}else{//se C5 não retornou nada...
			this.regSanguineo = new AipRegSanguineos();
			popularGrupoSanguineoExamesRealizados();
			popularFatorRhExamesRealizados();
		}
	}
	
	private void popularFatorRhExamesRealizados(){
		String fator = examesFacade.obterFatorRhExamesRealizados(pacCodigo);
		try{
			if(StringUtils.isNotEmpty(fator)){
				this.fatorRh = obterFatorRhPorCodigo(fator);
				habilitaFator = Boolean.FALSE;
			}
		}catch(IllegalArgumentException e){
			fatorRh = null;
		}
	}
	
	private void popularGrupoSanguineoExamesRealizados(){
		String grupo = examesFacade.obterFatorFatorSanguinioExamesRealizados(pacCodigo);
		try{
			if(StringUtils.isNotEmpty(grupo)){
				this.grupoSanguineo = DominioGrupoSanguineo.valueOf(grupo);
				habilitaGrupoSanguinio = Boolean.FALSE;
			}
		}catch(IllegalArgumentException e){//A consulta pode retornar grupo Indeterminado, que não existe no dominio.
			grupoSanguineo = null;
		}
	}
	
	/**
	 * Lista SuggestionBox AghCid
	 */
	public List<AghCid> pesquisarCid(String pesquisa) {
		return this.returnSGWithCount(this.transplanteFacade.pesquisarCidPorSeqCodDescricao(pesquisa),this.transplanteFacade.pesquisarCidPorSeqCodDescricaoCount(pesquisa));
	}
	
	/**
	 * Lista SuggestionBox MtxOrigens 
	 */
	public List<MtxOrigens> pesquisarMtxOrigens(String pesquisa) {
		return this.returnSGWithCount(this.transplanteFacade.pesquisarMtxOrigensPorSeqCodDescricao(pesquisa),this.transplanteFacade.pesquisarMtxOrigensPorSeqCodDescricaoCount(pesquisa));
	}
	
	/**
	 * Obtem Dominio do Grupo Sanguineo equivalente ao código obtido do Registro Sanguineo. 
	 * 
	 * @param codigo {@link String}
	 * @return {@link DominioGrupoSanguineo}
	 */
	private DominioGrupoSanguineo obterGrupoSanguineoPorCodigo(String codigo) {
		if(codigo != null){
			switch (codigo) {
			case "A": 
				return DominioGrupoSanguineo.A;
			case "B": 
				return DominioGrupoSanguineo.B;
			case "AB": 
				return DominioGrupoSanguineo.AB;
			case "O": 
				return DominioGrupoSanguineo.O;
			default:
				return null;	
			}
		}
		return null;
	}
	
	/**
	 * Obtem Dominio do Fator RH equivalente ao código obtido do Registro Sanguineo.
	 * 
	 * @param codigo {@link String}
	 * @return {@link DominioFatorRh}
	 */
	private DominioFatorRh obterFatorRhPorCodigo(String codigo) {
		if(codigo != null){
			switch (codigo) {
			case "+": 
				return DominioFatorRh.P;
			case "-": 
				return DominioFatorRh.N;
			default:
				return null;	
			}
		}
		return null;
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
	
	/**
	 * Calcula a diferença em anos entre a data de ingresso do paciente com a data atual.
	 * @throws ApplicationBusinessException 
	 */
	public void calcularIdadeIngresso(){
		
		this.idadeIngresso = StringUtils.EMPTY;
		if (this.transplante != null && this.paciente != null && this.paciente.getDtNascimento() != null && this.transplante.getDataIngresso() != null) {
			this.idadeIngresso = this.transplanteFacade.calcularIdadeIngresso(this.transplante.getDataIngresso(), this.paciente.getDtNascimento());
		}
		this.obterEscore();
	}
	
	/**
	 * Consulta para obter escore
	 */
	public String obterEscore() {
		this.escore = null;
		if (this.statusDoenca != null && this.statusDoenca.getSeq() != null && this.transplante.getDataIngresso() != null && this.paciente.getDtNascimento() != null) {
			this.escore = (int)this.transplanteFacade.obterEscore(this.statusDoenca.getSeq(), this.transplante.getDataIngresso(), this.paciente.getDtNascimento()).doubleValue();
			return String.valueOf(escore);
		}else{
			if (this.transplante.getDataIngresso() != null && this.paciente.getDtNascimento() != null) {
				this.escore = (int)this.transplanteFacade.obterEscore(0, this.transplante.getDataIngresso(), this.paciente.getDtNascimento()).doubleValue();
				return String.valueOf(escore);
			}
		}
		return StringUtils.EMPTY;
	}
	
	public void abrirModalConfirmacao(){
		
		if(this.transplante.getCodDoador() != null){
			this.modalConfirmacao2 = Boolean.TRUE;
			openDialog("modalConfirmacaoON02WG");
			return;
		}
		
		if(this.tipoTmo == DominioSituacaoTmo.U && this.doador != null && this.doadorFormatado != null){
			this.modalConfirmacao1 = Boolean.TRUE;
			openDialog("modalConfirmacaoON01WG");
			return;
		}
		
		if(this.tipoTmo != DominioSituacaoTmo.G){
			this.tipoAlogenico = null;
		}
		
		if(this.tipoAlogenico == DominioTipoAlogenico.N){
			this.desabilitaCodDoador = false;
		}
		
		this.transplante.setTipoTmo(this.tipoTmo);
	}
	
	public void limparDoador(){
		this.doador = new AipPacientes();
		this.doadorFormatado = null;
		this.fecharModalConfimacao();
	}
	
	public void limparCodigoDoador(){
		this.transplante.setCodDoador(null);
		this.desabilitaCodDoador = true;
		this.fecharModalConfimacao();
	}
	
	public void setarValorAnteriorTipoTmo(){
		this.tipoTmo = this.transplante.getTipoTmo();
	}
		
	public void fecharModalConfimacao(){
		
		if(this.modalConfirmacao1){
			this.modalConfirmacao1 = Boolean.FALSE;
			closeDialog("modalConfirmacaoON01WG");
		}
		
		if(this.modalConfirmacao2){
			this.modalConfirmacao2 = Boolean.FALSE;
			closeDialog("modalConfirmacaoON02WG");
		}
		
	}
	
	public String pesquisaFonetica() {
		pesquisaPacienteController.inicializar();
		pesquisaPacienteController.iniciar();
		pesquisaPacienteController.limparCampos();
		pesquisaPacienteController.setIncluirPacienteTMO(Boolean.TRUE);
	    return PAGE_PESQUISA_PACIENTE;
	}
	
	
	public String voltar(){
		if(PAGE_PACIENTES_LISTAS_TMO.equalsIgnoreCase(telaAnterior)) {	// #50814
			limpar();
			return PAGE_PACIENTES_LISTAS_TMO;
		}else if(telaAnterior.equals("paciente-pesquisaPaciente")){
			pesquisaPacienteController.setAipPaciente(pacienteRetorno);
			pesquisaPacienteController.setRespeitarOrdem(respeitarOrdemRetorno);
			pesquisaPacienteController.setIncluirPacienteTMO(Boolean.FALSE);
			if (realizouPesquisaFoneticaRetorno) {
				pesquisaPacienteController.pesquisarFonetica();
			}
			else {
				pesquisaPacienteController.pesquisar();
			}
		}
		limpar();
		grupoSanguineo = null;
		fatorRh = null;
		return this.telaAnterior;
	}
	
	public void gravar(){ 
		if(this.transplante != null && this.pacCodigo != null) {
			List<MtxTransplantes> listaTranplantados = obtemTransplantados(this.transplante.getTipoTmo(),this.pacCodigo); 
			if(!listaTranplantados.isEmpty()) {
				
				MtxTransplantes mtxTransplanteC7 = listaTranplantados.get(0);
				
				if(!mtxTransplanteC7.getSeq().equals(transplante.getSeq())){
					SimpleDateFormat formatoDataIngresso = new SimpleDateFormat("dd/MM/yyyy");
					String dataIngresso = formatoDataIngresso.format(mtxTransplanteC7.getDataIngresso());
					String allD = "O Tipo de transplante selecionado já foi cadastrado em "+dataIngresso+" para esse Paciente.";
					apresentarMsgNegocio(Severity.INFO,allD);
					return;
				}
			}
		}
		validaTransplanteParaGravar();
		
	}
	
	private List<MtxTransplantes> obtemTransplantados(DominioSituacaoTmo tipoTmo, Integer codPacienteReceptor) {
		return transplanteFacade.obtemTransplantados(tipoTmo,codPacienteReceptor);
		
	}
 	
		
	private void validaTransplanteParaGravar() {
		try{
			if (this.paciente != null && this.transplante != null) {
			
				this.validarTranplante();
				//RN3
				boolean criarRegistro = grupoSanguineoFatorAlterado();
				if (this.grupoSanguineo != null ) {
					this.regSanguineo.setGrupoSanguineo(this.grupoSanguineo.getDescricao());
				}
				if (this.fatorRh != null ) {
					this.regSanguineo.setFatorRh(this.fatorRh.getDescricao());
				}
				if(this.tipoAlogenico != null){
					this.transplante.setTipoAlogenico(tipoAlogenico);
				}

				this.transplanteFacade.persitirPacienteListaTransplanteTMO(this.transplante, this.regSanguineo, this.paciente, this.statusDoenca, criarRegistro);
				
				if (this.editandoTransplante) {
					apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EDICAO);
				} else {
					this.limpar();
					apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_INCLUSAO);
					
				}
				
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		}	
	}
		
	private void limpar(){
		this.transplante = new MtxTransplantes();
		this.doador = new AipPacientes();
		this.doadorFormatado = null;
		this.escore = null;
		this.idadeIngresso = StringUtils.EMPTY;
		this.aghCid = null;
		this.mtxOrigens = null;
		this.tipoTmo = null;
		this.modalConfirmacao1 = Boolean.FALSE;
		this.modalConfirmacao2 = Boolean.FALSE;
		inicializarRegSanguineo(this.pacCodigo);
		this.statusDoenca = null;
		this.listaStatusDoenca = null;
		this.tipoAlogenico = null;
		this.desabilitaCodDoador = false;
	}
	
	public void validarTranplante() throws ApplicationBusinessException{
		
		if (DateUtil.validaDataMaior(this.paciente.getDtNascimento(), this.transplante.getDataIngresso())) {
			throw new ApplicationBusinessException(IncluirPacienteListaTransplanteTMOControllerExceptionCode.ERRO_INGRESSO_MENOR_NASCIMENTO);
		}
		if (DateUtil.validaDataMaior(this.paciente.getDtNascimento(), this.transplante.getDataDiagnostico())) {
			throw new ApplicationBusinessException(IncluirPacienteListaTransplanteTMOControllerExceptionCode.ERRO_DIAGNOSTICO_MENOR_NASCIMENTO);
		}
		if (!this.editandoTransplante) {
			this.transplante.setReceptor(this.paciente);
		}
		if (this.doador != null && this.doador.getCodigo() != null) {
			this.transplante.setDoador(this.doador);
		}
		if (this.mtxOrigens != null && this.mtxOrigens.getSeq() != null) {
			this.transplante.setOrigem(mtxOrigens);
		}
		if (this.aghCid != null && this.aghCid.getSeq() != null) {
			this.transplante.setCid(aghCid);
		}
	}
	
	/**
	 * Validar RN3 Criar um registro aip_reg_sanguineo, caso seja alterado o grupo sanguíneo ID04 ou o fator RH ID05 
	 * 
	 * @return
	 */
	private boolean grupoSanguineoFatorAlterado(){
		
		if(this.regSanguineo != null && this.grupoSanguineo != null && this.fatorRh != null
		    && this.regSanguineo.getGrupoSanguineo() != null && this.regSanguineo.getFatorRh() != null
			&& this.regSanguineo.getGrupoSanguineo().equals(this.grupoSanguineo.getDescricao()) && this.regSanguineo.getFatorRh().equals(this.fatorRh)){
				return false;
		}
		return true;
	}
	
	public String abrirCidPorCapitulo(){
		vindoDaTelaCIDCapitulo = true;
		return PAGE_CID_POR_CAPITULO;
	}
	
    public void carregarStatusDoencaPaciente(){
    	listaStatusDoenca = null;
    	if(!this.editandoTransplante){
    		this.statusDoenca = null;
    	}
    	if(tipoTmo != null){
    		listaStatusDoenca = transplanteFacade.obterStatusDoencaPaciente(this.tipoTmo);
    	}	
    }
	
	//Gets e Sets
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public String getTelaAnterior() {
		return telaAnterior;
	}
	public void setTelaAnterior(String telaAnterior) {
		this.telaAnterior = telaAnterior;
	}
	public AipPacientes getPaciente() {
		return paciente;
	}
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	public AghCid getAghCid() {
		return aghCid;
	}
	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}
	public MtxOrigens getMtxOrigens() {
		return mtxOrigens;
	}
	public void setMtxOrigens(MtxOrigens mtxOrigens) {
		this.mtxOrigens = mtxOrigens;
	}
	public AipRegSanguineos getRegSanguineo() {
		return regSanguineo;
	}
	public void setRegSanguineo(AipRegSanguineos regSanguineo) {
		this.regSanguineo = regSanguineo;
	}
	public MtxTransplantes getTransplante() {
		return transplante;
	}
	public void setTransplante(MtxTransplantes transplante) {
		this.transplante = transplante;
	}
	public String getIdadeIngresso() {
		return idadeIngresso;
	}
	public void setIdadeIngresso(String idadeIngresso) {
		this.idadeIngresso = idadeIngresso;
	}
	public Integer getEscore() {
		return escore;
	}
	public void setEscore(Integer escore) {
		this.escore = escore;
	}
	public AipPacientes getDoador() {
		return doador;
	}
	public void setDoador(AipPacientes doador) {
		this.doador = doador;
	}
	public String getDoadorFormatado() {
		return doadorFormatado;
	}
	public void setDoadorFormatado(String doadorFormatado) {
		this.doadorFormatado = doadorFormatado;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public AipPacientes getPacienteRetorno() {
		return pacienteRetorno;
	}
	public void setPacienteRetorno(AipPacientes pacienteRetorno) {
		this.pacienteRetorno = pacienteRetorno;
	}
	public Boolean getRespeitarOrdemRetorno() {
		return respeitarOrdemRetorno;
	}
	public void setRespeitarOrdemRetorno(Boolean respeitarOrdemRetorno) {
		this.respeitarOrdemRetorno = respeitarOrdemRetorno;
	}
	public Boolean getRealizouPesquisaFoneticaRetorno() {
		return realizouPesquisaFoneticaRetorno;
	}
	public void setRealizouPesquisaFoneticaRetorno(Boolean realizouPesquisaFoneticaRetorno) {
		this.realizouPesquisaFoneticaRetorno = realizouPesquisaFoneticaRetorno;
	}
	public DominioGrupoSanguineo getGrupoSanguineo() {
		return grupoSanguineo;
	}
	public void setGrupoSanguineo(DominioGrupoSanguineo grupoSanguineo) {
		this.grupoSanguineo = grupoSanguineo;
	}
	public DominioFatorRh getFatorRh() {
		return fatorRh;
	}
	public void setFatorRh(DominioFatorRh fatorRh) {
		this.fatorRh = fatorRh;
	}
	public DominioSituacaoTmo getTipoTmo() {
		return tipoTmo;
	}
	public void setTipoTmo(DominioSituacaoTmo tipoTmo) {
		this.tipoTmo = tipoTmo;
	}
	public boolean isEditandoTransplante() {
		return editandoTransplante;
	}
	public void setEditandoTransplante(boolean editandoTransplante) {
		this.editandoTransplante = editandoTransplante;
	}
	public Boolean getModalConfirmacao1() {
		return modalConfirmacao1;
	}
	public void setModalConfirmacao1(Boolean modalConfirmacao1) {
		this.modalConfirmacao1 = modalConfirmacao1;
	}
	public Boolean getModalConfirmacao2() {
		return modalConfirmacao2;
	}
	public void setModalConfirmacao2(Boolean modalConfirmacao2) {
		this.modalConfirmacao2 = modalConfirmacao2;
	}

	public MtxCriterioPriorizacaoTmo getStatusDoenca() {
		return statusDoenca;
	}

	public void setStatusDoenca(MtxCriterioPriorizacaoTmo statusDoenca) {
		this.statusDoenca = statusDoenca;
	}

	public List<MtxCriterioPriorizacaoTmo> getListaStatusDoenca() {
		return listaStatusDoenca;
	}

	public void setListaStatusDoenca(
			List<MtxCriterioPriorizacaoTmo> listaStatusDoenca) {
		this.listaStatusDoenca = listaStatusDoenca;
	}

	public DominioTipoAlogenico getTipoAlogenico() {
		return tipoAlogenico;
	}

	public void setTipoAlogenico(DominioTipoAlogenico tipoAlogenico) {
		this.tipoAlogenico = tipoAlogenico;
	}

	public boolean isDesabilitaCodDoador() {
		return desabilitaCodDoador;
	}

	public void setDesabilitaCodDoador(boolean desabilitaCodDoador) {
		this.desabilitaCodDoador = desabilitaCodDoador;
	}

	public Boolean getHabilitaGrupoSanguinio() {
		return habilitaGrupoSanguinio;
	}

	public void setHabilitaGrupoSanguinio(Boolean habilitaGrupoSanguinio) {
		this.habilitaGrupoSanguinio = habilitaGrupoSanguinio;
	}

	public Boolean getHabilitaFator() {
		return habilitaFator;
	}

	public void setHabilitaFator(Boolean habilitaFator) {
		this.habilitaFator = habilitaFator;
	}
	
}
