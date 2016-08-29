package br.gov.mec.aghu.prescricaomedica.action;

import java.net.UnknownHostException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterPrescricaoCuidadoController extends ActionController {

	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "manterPrescricaoMedica";

	private static final Log LOG = LogFactory.getLog(ManterPrescricaoCuidadoController.class);

	private static final long serialVersionUID = 2124997154296418069L;

	public enum ManterPrescricaoCuidadoControllerExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_TIPO_NAO_EXISTE
	}

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
		
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private PrescricaoMedicaVO prescricaoMedicaVO;
	
	private int idConversacaoAnterior;
	
	private Object cuidadoUsualPesquisa;

	private MpmPrescricaoMedica prescricaoMedica;
	private AghUnidadesFuncionais unidadeFuncional;
	private MpmPrescricaoCuidado prescricaoCuidado;
	
	// Utilizado para verficar se houve alguma alteração efetiva na edição do
	// cuidado.
	// Cada alteração de um cuidado, gera um desdobramento de ind_pendente de P
	// para A, tais desdobramentos são relacionados no realtório de
	// modificações da prescrição.
	private MpmPrescricaoCuidado prescricaoCuidadoOriginal;

	private boolean altera = false;
	//parametros do mestre
	private Integer atdSeq;
	private Long seq = null;

	private List<MpmPrescricaoCuidado> listaCuidadosPrescritos = new ArrayList<MpmPrescricaoCuidado>();
	private Map<MpmPrescricaoCuidado, Boolean> prescricaoCuidadosSelecionados = new HashMap<MpmPrescricaoCuidado, Boolean>();

	// usado na frequencia
	private Integer frequencia;
	private MpmTipoFrequenciaAprazamento tipoAprazamento;
	
	private Boolean confirmaVoltar;
	private boolean confirmaEditar;
	private boolean ocorreuAlteracao;
	private Long lastClickedSeq;
			
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		
		MpmPrescricaoMedicaId idPrescricaoMedica = new MpmPrescricaoMedicaId();
		idPrescricaoMedica.setAtdSeq(this.prescricaoMedicaVO.getId()
				.getAtdSeq());
		idPrescricaoMedica.setSeq(this.prescricaoMedicaVO.getId().getSeq());
		
		this.prescricaoMedica = this.prescricaoMedicaFacade
				.obterPrescricaoMedicaPorId(idPrescricaoMedica);

		AghAtendimentos atd = this.aghuFacade
				.obterAghAtendimentoPorChavePrimaria(this.prescricaoMedicaVO
						.getId().getAtdSeq());
		
		if (atd != null) {
			this.unidadeFuncional = atd.getUnidadeFuncional();
		}
		
		this.atualizarListaCuidadosPrescritos();
		this.lastClickedSeq = null;
		//Edicao de Cuidado chamada pela tela Manter Prescrições Médicas
		if(this.seq != null){
			preparaAlterar(false, seq);
		}else{
			this.novoItem();
		}
	
	}

	private void atualizarListaCuidadosPrescritos() {
		
		if (this.prescricaoMedica != null
				&& this.prescricaoMedica.getId() != null) {
			this.listaCuidadosPrescritos = this.prescricaoMedicaFacade
					.obterListaCuidadosPrescritos(
							this.prescricaoMedica.getId(),
							this.prescricaoMedica.getDthrFim());
			
			final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
			Collections.sort(this.listaCuidadosPrescritos, new Comparator<MpmPrescricaoCuidado>(){
						public int compare(
								MpmPrescricaoCuidado mpmPrescricaoCuidado1,
								MpmPrescricaoCuidado mpmPrescricaoCuidado2) {

							return collator.compare(mpmPrescricaoCuidado1.getDescricaoFormatada(),
									mpmPrescricaoCuidado2.getDescricaoFormatada());
						}
			});
			
			for (MpmPrescricaoCuidado cuidado : this.listaCuidadosPrescritos) {
				this.prescricaoCuidadosSelecionados.put(cuidado, false);
			}
		} else {
			this.listaCuidadosPrescritos = new ArrayList<MpmPrescricaoCuidado>(0);
			this.prescricaoCuidadosSelecionados = new HashMap<MpmPrescricaoCuidado, Boolean>();
		}
	}

	public void gravar() {
		if (!this.altera) {
			incluir();
		} else {
			alterar();
		}
	}

	public void populaAprazamentoPadrao() {
		//Senão tiver preenchido o campo Tipo de Frequencia de Aprazamneto
		if(this.tipoAprazamento == null){
			if (this.prescricaoCuidado != null
					&& this.prescricaoCuidado.getMpmCuidadoUsuais() != null
					&& this.prescricaoCuidado.getMpmCuidadoUsuais()
							.getMpmTipoFreqAprazamentos() != null) {
				this.tipoAprazamento = this.prescricaoCuidado
						.getMpmCuidadoUsuais().getMpmTipoFreqAprazamentos();
				if (this.prescricaoCuidado.getMpmCuidadoUsuais().getFrequencia() != null) {
					this.frequencia = this.prescricaoCuidado.getMpmCuidadoUsuais()
							.getFrequencia().intValue();
				}
			}
		}
	}

	private void incluir() {
		try {

			validaAprazamento();
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			this.prescricaoMedicaFacade.inserir(prescricaoCuidado, nomeMicrocomputador, new Date());
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_INCLUSAO_PRESCRICAO_CUIDADO");
			
			this.limpar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void alterar() {
		try {
			// Verifica se foram realizadas alterações nos campos da tela,
			// em relação aos atributos do registro original.
			if(this.prescricaoCuidadoOriginal != null){
				if(CoreUtil.igual(this.prescricaoCuidadoOriginal.getFrequencia(), this.frequencia) &&
				   CoreUtil.igual(this.prescricaoCuidadoOriginal.getMpmTipoFreqAprazamentos(), this.tipoAprazamento) &&
				   CoreUtil.igual(this.prescricaoCuidadoOriginal.getDescricao(),this.prescricaoCuidado.getDescricao())){
					
					apresentarMsgNegocio(Severity.WARN,
							"MENSAGEM_ALTERACAO_NAO_REALIZADA");
					
					this.lastClickedSeq = null;
					this.confirmaEditar = false;
					this.ocorreuAlteracao = false;
					this.confirmaVoltar = false;
					return;
				}
			}

			prescricaoCuidado.setServidor(this.servidorLogadoFacade.obterServidorLogado());
			validaAprazamento();
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			this.prescricaoMedicaFacade.alterarPrescricaoCuidado(prescricaoCuidado, nomeMicrocomputador, new Date());
			
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_ALTERACAO_PRESCRICAO_CUIDADO",
					prescricaoCuidado.getDescricaoFormatada());
			
			this.limpar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluirPrescricaoCuidadosSelecionados() {
		if(prescricaoCuidadosSelecionados != null){
			int nroPrescricoesCuidadoRemovidas = 0;
			
			try {				
				for (MpmPrescricaoCuidado prescCuidado: listaCuidadosPrescritos){
					if (prescricaoCuidadosSelecionados.get(prescCuidado) == true){
						this.prescricaoMedicaFacade.removerPrescricaoCuidado(prescCuidado);
						nroPrescricoesCuidadoRemovidas++;
					}
				}	
			} catch ( BaseException e) {
				apresentarExcecaoNegocio(e);
				return;
			}	
			
			if (nroPrescricoesCuidadoRemovidas > 0){
				if (nroPrescricoesCuidadoRemovidas > 1){
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_PRESCRICAO_CUIDADOS");
				
				}else{
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_PRESCRICAO_CUIDADO");
				}
				
			}else{
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_NENHUMA_PRESCRICAO_CUIDADO_SELECIONADA_REMOCAO");
			}
			
			this.limpar();
		}
		else {
			this.confirmaEditar = false;
			this.confirmaVoltar = false;
			this.ocorreuAlteracao = false;
		}
	}
		
	public void preparaAlterarLastClickedSeq() {	
		this.prescricaoCuidado.setDescricao(this.prescricaoCuidadoOriginal.getDescricao());
		this.preparaAlterar(true, this.lastClickedSeq);
	}
	
	public void preparaAlterar(Boolean forceEdit, Long seq) {
		this.lastClickedSeq = seq;
		
		if (forceEdit) {
			this.confirmaEditar = false;
			this.ocorreuAlteracao = true;
		}
		
		if (seq != null) {
			if (forceEdit || !existeAlteracaoPendente()) {
				
				this.seq = seq;
				this.prescricaoCuidado = this.prescricaoMedicaFacade
						.obterPrescricaoCuidado(this.prescricaoMedica.getId()
								.getAtdSeq(), this.seq);
				//controle caso o item tenha sido excluído por outro usuário
				if(prescricaoCuidado == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					novoItem();
					return;
				}
				this.prescricaoCuidadoOriginal = null;
				
				try{
					this.prescricaoCuidadoOriginal = (MpmPrescricaoCuidado) this.prescricaoCuidado.clone();
				}catch (CloneNotSupportedException e) {
					LOG.error("A classe MpmPrescricaoCuidado "
							+ "não implementa a interface Cloneable.", e);
				}
				
				this.frequencia = this.prescricaoCuidado.getFrequencia();
				
				if (this.prescricaoCuidado.getMpmTipoFreqAprazamentos() != null) {
					this.tipoAprazamento = this.prescricaoCuidado
							.getMpmTipoFreqAprazamentos();
					
				}
				this.altera = true;
				this.confirmaEditar = false;
				this.confirmaVoltar = false;
				this.ocorreuAlteracao = false;
				this.atualizarListaCuidadosPrescritos();
			}
			else {
				this.confirmaEditar = true;
			}
		}		
		if (confirmaEditar && this.seq != null){
			openDialog("modalEditConfirmacaoPendenciaWG");
		}
	}

	private void novoItem() {
		this.confirmaVoltar = false;
		this.confirmaEditar = false;
		this.ocorreuAlteracao = false;
		this.frequencia = null;
		this.lastClickedSeq = null;
		this.prescricaoCuidado = new MpmPrescricaoCuidado();
		this.prescricaoCuidado.setPrescricaoMedica(this.prescricaoMedica);
		this.prescricaoCuidado.setServidor(this.servidorLogadoFacade.obterServidorLogado());
	}

	public List<MpmCuidadoUsual> obterCuidados(String cuidadoUsualPesquisa) {
		return this.returnSGWithCount(this.prescricaoMedicaFacade.getListaCuidadosUsuaisAtivosUnidade(
				cuidadoUsualPesquisa, this.unidadeFuncional),obterCuidadosCount(cuidadoUsualPesquisa));
	}
	
	public Long obterCuidadosCount(String cuidadoUsualPesquisa) {
		return this.prescricaoMedicaFacade
				.getListaCuidadosUsuaisAtivosUnidadeCount(cuidadoUsualPesquisa,
						this.unidadeFuncional);
	}

	public String voltar() {
		limpar();				
		return PAGINA_MANTER_PRESCRICAO_MEDICA;
	}
	
	public void possuiPendencias() {
		this.setOcorreuAlteracao(true);
		this.confirmaEditar = true;
		this.confirmaVoltar = true;
	}
	
	public void populaAprazamentoPadraoEPendencias() {
		populaAprazamentoPadrao();
		possuiPendencias();
	}
	
	public Boolean existeAlteracaoPendente() {
		return (this.ocorreuAlteracao);
	}
	
	public String verificaPendencias(){
		if (existeAlteracaoPendente()) {
			confirmaVoltar = true;
			return null;
		}
		return voltar();
	}
	
	public void limpar() {
        this.frequencia = null;
        this.seq = null;
        this.lastClickedSeq = null;
        this.tipoAprazamento = null;
        this.altera = false;
        this.confirmaVoltar = false;
        this.confirmaEditar = false;
        this.ocorreuAlteracao = false;
        this.novoItem();
        this.atualizarListaCuidadosPrescritos();
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(String strPesquisa) {
		return this.returnSGWithCount(this.prescricaoMedicaFacade.buscarTipoFrequenciaAprazamento(strPesquisa),
										this.prescricaoMedicaFacade.buscarTipoFrequenciaAprazamentoCount(strPesquisa));
	}
	
	
	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}

	public String buscaDescricaoTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento
				.getDescricaoSintaxeFormatada(this.frequencia != null ? this.frequencia
						.shortValue()
						: null)
				: "";
	}
	
	public boolean verificaRequiredFrequencia() {
		return this.tipoAprazamento != null
				&& this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {
		if (!this.verificaRequiredFrequencia()) {
			this.frequencia = null;
		}
	}

	private void validaAprazamento() {
		prescricaoCuidado.setFrequencia(this.frequencia);
		prescricaoCuidado.setMpmTipoFreqAprazamentos(this.tipoAprazamento);
	}
	
	// getters and setters
	public MpmPrescricaoCuidado getPrescricaoCuidado() {
		return prescricaoCuidado;
	}

	public void setPrescricaoCuidado(MpmPrescricaoCuidado prescricaoCuidado) {
		this.prescricaoCuidado = prescricaoCuidado;
	}

	public Object getCuidadoUsualPesquisa() {
		return cuidadoUsualPesquisa;
	}

	public void setCuidadoUsualPesquisa(Object cuidadoUsualPesquisa) {
		this.cuidadoUsualPesquisa = cuidadoUsualPesquisa;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return this.prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Integer getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}

	public List<MpmPrescricaoCuidado> getListaCuidadosPrescritos() {
		return listaCuidadosPrescritos;
	}

	public void setListaCuidadosPrescritos(
			List<MpmPrescricaoCuidado> listaCuidadosPrescritos) {
		this.listaCuidadosPrescritos = listaCuidadosPrescritos;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Map<MpmPrescricaoCuidado, Boolean> getPrescricaoCuidadosSelecionados() {
		return prescricaoCuidadosSelecionados;
	}

	public void setPrescricaoCuidadosSelecionados(
			Map<MpmPrescricaoCuidado, Boolean> prescricaoCuidadosSelecionados) {
		this.prescricaoCuidadosSelecionados = prescricaoCuidadosSelecionados;
	}
	
	public Boolean getConfirmaVoltar() {
		return confirmaVoltar;
	}

	public void setConfirmaVoltar(Boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public MpmPrescricaoCuidado getPrescricaoCuidadoOriginal() {
		return prescricaoCuidadoOriginal;
	}

	public void setPrescricaoCuidadoOriginal(
			MpmPrescricaoCuidado prescricaoCuidadoOriginal) {
		this.prescricaoCuidadoOriginal = prescricaoCuidadoOriginal;
	}

	public boolean isConfirmaEditar() {
		return confirmaEditar;
	}

	public void setConfirmaEditar(boolean confirmaEditar) {
		this.confirmaEditar = confirmaEditar;
	}

	public Long getLastClickedSeq() {
		return lastClickedSeq;
	}

	public void setLastClickedSeq(Long lastClickedSeq) {
		this.lastClickedSeq = lastClickedSeq;
	}

	public boolean isOcorreuAlteracao() {
		return ocorreuAlteracao;
	}

	public void setOcorreuAlteracao(boolean ocorreuAlteracao) {
		this.ocorreuAlteracao = ocorreuAlteracao;
	}
}
