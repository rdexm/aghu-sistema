package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;


public class SelecionarPrescricaoConsultarController extends ActionController {

    private static final Log LOG = LogFactory.getLog(SelecionarPrescricaoConsultarController.class);

	private static final String ELABORACAO_PRESCRICAO_ENFERMAGEM = "elaboracaoPrescricaoEnfermagem";

	private static final String MANTER_PRESCRICAO_ENFERMAGEM = "manterPrescricaoEnfermagem";

	private static final String LISTAR_PACIENTES_INTERNADOS = "listarPacientesInternados";

	private static final String PRESCRICAOENFERMAGEM_LISTA_PACIENTES_ENFERMAGEM = "prescricaoenfermagem-listaPacientesEnfermagem";
	private static final String SELECIONAR_PRESCRICAO_CONSULTAR = "prescricaomedica-selecionarPrescricaoConsultar";
	private static final String LISTA_ATENDIMENTO_DIVERSO = "exames-atendimentoDiversoList";

	private static final long serialVersionUID = -1908925376684731510L;
	
//	private static final String RELATORIO_CONTRA_CHEQUE_PRESCRICAO_MEDICA     = "prescricaomedica-relatorioContraChequePrescricaoMedica";
	private static final String CONTROLE_PACIENTE_LISTAR_PACIENTES_INTERNADOS = "controlepaciente-listarPacientesInternados";
	
	private static final String PRONTUARIO_ONLINE = "prontuarioOnline";

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private AghAtendimentos atendimento;
	
	private AipPacientes paciente;
	
	private Date dtReferencia;
	
	private Integer prescricaoMedicaSeq;
	
	private Integer itemPrescricaoId;
	
	private Integer indice = -1;
	
	private MpmPrescricaoMedica prescricaoMedica;
	
	private ItemPrescricaoMedicaVO itemPrescricaoMedica;
	
	private List<MpmPrescricaoMedica> listaPrescricoes = new ArrayList<MpmPrescricaoMedica>(0);
	
	private List<ItemPrescricaoMedicaVO> listaItensPrescricao = new ArrayList<ItemPrescricaoMedicaVO>();
	
	private List<AghAtendimentos> atendimentos;
	
	private int sizeListaItensPrescricao;
	
	private PrescricaoMedicaVO prescricaoMedicaVO;
	
	private Integer codigoPaciente;
	
	private Integer prontuario;
	
	private String voltarPara;
	
	private Integer penSeq;
	
	private Integer penAtdSeq;
	
	private int idConversacaoAnterior;
	
	private int sizeListaPrescricoes;
	
	//Variaveis adicionadas da para a naveçação informações apenas para navegação
		private Integer atdSeq;
		private String voltarPara2;
	
	private Boolean showModalAtendimentos;

	@Inject
	private RelatorioPrescricaoMedicaController relatorioPrescricaoMedicaController;
	
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

		CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
		
		if (codPac != null && codPac.getCodigo() > 0) {
			codigoPaciente = codPac.getCodigo();
		}

		if(codigoPaciente != null){
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(codigoPaciente);
			
		} else if (prontuario != null) {
			this.paciente = this.pacienteFacade.obterPacientePorProntuario(prontuario);
		} 
		
//		if (this.paciente != null) {
//			atendimento = prescricaoMedicaFacade.obterAtendimentoPorProntuario(paciente.getProntuario());
//			carregarPrescricoes();
//		}
		
		setarCamposAtendimento();
	
	}
	
	public void setarCamposAtendimento() {
		if (paciente != null) {
			codigoPaciente = paciente.getCodigo();
			prontuario = paciente.getProntuario();
			atendimentos = ambulatorioFacade.pesquisarAtendimentoParaPrescricaoMedica(paciente.getCodigo(), atdSeq);
			if(atendimentos != null && !atendimentos.isEmpty()) {
				if(atendimentos.size() == 1) {
					atendimento = atendimentos.get(0);
					carregarPrescricoes();
				} else {
					apresentarMsgNegocio(Severity.ERROR, "VERIFICAR_ATENDIMENTO");
				}
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ATENDIMENTO_NAO_ENCONTRADO_PARA_PACIENTE");
			}
		} else {
			limparCampos();
		}
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
	
	public void limparCampos() {
		listaPrescricoes = new ArrayList<MpmPrescricaoMedica>(0);
		listaItensPrescricao = new ArrayList<ItemPrescricaoMedicaVO>();
		paciente = null;
		atendimentos = null;
		atendimento = null;
		prescricaoMedicaSeq = null;
		itemPrescricaoMedica = null;
		itemPrescricaoId = 0;
		dtReferencia = null;
		indice = -1;
		codigoPaciente = null;
		prontuario = null;
		showModalAtendimentos = false;
		this.prescricaoMedica = null;
	}
	
	public List<AipPacientes> pesquisarPaciente(Object objParam) throws ApplicationBusinessException {
		String strPesquisa = (String) objParam;
		Integer prontuario = null;
		if(!StringUtils.isBlank(strPesquisa)){
			prontuario = Integer.valueOf(strPesquisa);
			this.setPaciente(pacienteFacade.obterPacientePorProntuario(prontuario));
		}
		List<AipPacientes> listaPacientes = new ArrayList<AipPacientes>(0);
		if(this.paciente != null) {
			listaPacientes.add(paciente);
		}
		
		return listaPacientes; 
	}

	public void filtrarPrescricoes() {
		try {
			MpmPrescricaoMedica prescricaoSelecionada = null;
			if(this.listaPrescricoes != null && !this.listaPrescricoes.isEmpty()) {
				if(dtReferencia != null) {
					prescricaoSelecionada = (MpmPrescricaoMedica)CollectionUtils.find(this.listaPrescricoes, new Predicate() {  
						public boolean evaluate(Object o) {  
							if(DateUtils.truncate(((MpmPrescricaoMedica)o).getDtReferencia(), Calendar.DATE).compareTo(dtReferencia) == 0) {								
								return true;  
							}
							return false;  
						}  
					});
				}
				if(prescricaoSelecionada != null) {
					this.prescricaoMedica = prescricaoSelecionada;
					this.carregarItens();
					this.indice = this.listaPrescricoes.indexOf(prescricaoSelecionada);
					if(this.indice.equals(listaPrescricoes.size()-1)) {
						this.indice--;
					}
					return;
					
				} else {
					apresentarMsgNegocio(Severity.ERROR, "Nenhuma prescrição encontrada para data informada.");
				}
				this.indice = this.listaPrescricoes.indexOf(prescricaoMedica);
				if(this.indice.equals(listaPrescricoes.size()-1)) {
					this.indice--;
				}
				return;
			}
			this.indice = -1;
		} catch (BaseException e) {
			this.indice = -1;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void carregarPrescricoes() {
		try {
			this.dtReferencia = null;
			if(paciente != null && atendimento != null && !addPrescricaoMedicasAAtendimento().isEmpty()) {
				listaPrescricoes = 	new ArrayList<MpmPrescricaoMedica>(atendimento.getMpmPrescricaoMedicases());
					final BeanComparator prescricaoSorter = new BeanComparator("dtReferencia", new ReverseComparator());
					Collections.sort(listaPrescricoes, prescricaoSorter);
					
					this.prescricaoMedica = listaPrescricoes.get(0);
					this.carregarItens();

				
			}
			else {
				this.listaPrescricoes = new ArrayList<MpmPrescricaoMedica>(0);
				this.listaItensPrescricao = new ArrayList<ItemPrescricaoMedicaVO>();
				this.prescricaoMedicaSeq = null;
				this.itemPrescricaoMedica = null;
				this.itemPrescricaoId = 0;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	

	

	private List<MpmPrescricaoMedica> addPrescricaoMedicasAAtendimento() {
		List<MpmPrescricaoMedica> lista = new ArrayList<MpmPrescricaoMedica>();
		if (atendimento != null) {
			lista = prescricaoMedicaFacade.pesquisaPrescricoesMedicasPorAtendimento(atendimento.getSeq());
			Set<MpmPrescricaoMedica> prescricoes = new HashSet<MpmPrescricaoMedica>();
			prescricoes.addAll(lista);
			atendimento.setMpmPrescricaoMedicases(prescricoes);
		}
		
		return lista;
	}
	
	public String getTruncProntuarioNomePaciente(Long size){
		if(paciente != null){
			return StringUtil.trunc(paciente.getProntuario() + " - " + paciente.getNome(), Boolean.TRUE, size);
		}
		return "";
	}
	
	public void processarSelecaoAtendimento(AghAtendimentos atendimento) throws ApplicationBusinessException {
		this.atendimento = atendimento;
		carregarPrescricoes();
	}
	
	public void carregarItens() throws BaseException {
		
		prescricaoMedicaSeq = prescricaoMedica.getId().getSeq();
		
		listaItensPrescricao = this.prescricaoMedicaFacade.buscarItensPrescricaoMedica(prescricaoMedica.getId(), true);
		sizeListaItensPrescricao = listaItensPrescricao.size();

		itemPrescricaoId = 0;
		
		if(listaItensPrescricao != null && !listaItensPrescricao.isEmpty()){
			itemPrescricaoMedica = listaItensPrescricao.get(0);
		}
		else {
			itemPrescricaoMedica = null;
		}
	}
	
	/*public void selecionarPrescricao(MpmPrescricaoMedica prescricao) {
		try {
			this.prescricaoMedica = prescricao;
			this.carregarItens();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}			
	}*/
	
	/*public void selecionarItemPrescricao(Integer index) {
		if(listaItensPrescricao != null && !listaItensPrescricao.isEmpty()){
			itemPrescricaoMedica = listaItensPrescricao.get(index);
			itemPrescricaoId = index;
		}
	}*/
	
	public String getLocalizacao() {
		if(this.atendimento != null && this.atendimento.getLeito() != null) {
			return "L: " +this.atendimento.getLeito().getLeitoID();
		}
		return null;
	}
	
	public String buscarNomeUsual(Short pVinCodigo, Integer pMatricula) {
		if(pVinCodigo != null && pVinCodigo != 0 && pMatricula != null && pMatricula != 0) {
			return this.pesquisaInternacaoFacade.buscarNomeUsual(pVinCodigo, pMatricula);
		}
		return null;
	}
	
	public String cancelar() {
		this.limparCampos();
		this.codigoPaciente = null;
		this.prontuario = null;
		
		if (ELABORACAO_PRESCRICAO_ENFERMAGEM.equalsIgnoreCase(voltarPara)) {
			return ELABORACAO_PRESCRICAO_ENFERMAGEM;
		} else if(MANTER_PRESCRICAO_ENFERMAGEM.equalsIgnoreCase(voltarPara)){
			return MANTER_PRESCRICAO_ENFERMAGEM;
		} else if(CONTROLE_PACIENTE_LISTAR_PACIENTES_INTERNADOS.equalsIgnoreCase(voltarPara)){
			return CONTROLE_PACIENTE_LISTAR_PACIENTES_INTERNADOS;
		} else if(LISTAR_PACIENTES_INTERNADOS.equalsIgnoreCase(voltarPara)) {
			return LISTAR_PACIENTES_INTERNADOS;
		} else if(PRESCRICAOENFERMAGEM_LISTA_PACIENTES_ENFERMAGEM.equalsIgnoreCase(voltarPara)){
			return PRESCRICAOENFERMAGEM_LISTA_PACIENTES_ENFERMAGEM;
		} else if(LISTA_ATENDIMENTO_DIVERSO.equalsIgnoreCase(voltarPara)) {
			return LISTA_ATENDIMENTO_DIVERSO;
		} else if(PRONTUARIO_ONLINE.equalsIgnoreCase(voltarPara)) {
			return PRONTUARIO_ONLINE;
		}
		
		return voltarPara;
	}
	
	public String imprimirContraCheque(MpmPrescricaoMedica prescricao) {
		try {
			setPrescricaoMedica(prescricao);
			
			this.prescricaoMedicaVO = this.prescricaoMedicaFacade.buscarDadosCabecalhoContraCheque(this.prescricaoMedica,listaItensPrescricao.isEmpty());
			this.prescricaoMedicaVO.setItens(listaItensPrescricao);
			
			relatorioPrescricaoMedicaController.setTipoImpressao(EnumTipoImpressao.REIMPRESSAO);
			relatorioPrescricaoMedicaController.setPrescricaoMedicaVO(prescricaoMedicaVO);
			
			relatorioPrescricaoMedicaController.imprimirContraCheque();

//			if(!redireciona) {
//				return RELATORIO_CONTRA_CHEQUE_PRESCRICAO_MEDICA;
//			}
			
		} catch (BaseException e) {
            LOG.error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
            LOG.error("Exceção capturada: ", e);
            apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NAO_FOI_POSSIVEL_IMPRIMIR_CONTRACHEQUE");
        }

		return SELECIONAR_PRESCRICAO_CONSULTAR;
	}

	public String redirecionarPesquisaFonetica(){
		return PESQUISA_FONETICA;
	}
	
	public Boolean getPesquisaFoneticaAtiva(){
		if("prontuarioOnline".equalsIgnoreCase(voltarPara)) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;

	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public Date getDtReferencia() {
		return dtReferencia;
	}

	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public List<MpmPrescricaoMedica> getListaPrescricoes() {
		return listaPrescricoes;
	}

	public Integer getPrescricaoMedicaSeq() {
		return prescricaoMedicaSeq;
	}

	public void setPrescricaoMedicaSeq(Integer prescricaoMedicaSeq) {
		this.prescricaoMedicaSeq = prescricaoMedicaSeq;
	}

	public Integer getItemPrescricaoId() {
		return itemPrescricaoId;
	}

	public void setItemPrescricaoId(Integer itemPrescricaoId) {
		this.itemPrescricaoId = itemPrescricaoId;
	}

	public List<ItemPrescricaoMedicaVO> getListaItensPrescricao() {
		return listaItensPrescricao;
	}

	public ItemPrescricaoMedicaVO getItemPrescricaoMedica() {
		return itemPrescricaoMedica;
	}

	public void setItemPrescricaoMedica(ItemPrescricaoMedicaVO itemPrescricaoMedica) {
		this.itemPrescricaoMedica = itemPrescricaoMedica;
	}

	public Integer getIndice() {
		return indice;
	}

	public void setIndice(Integer indice) {
		this.indice = indice;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getPenSeq() {
		return penSeq;
	}

	public void setPenSeq(Integer penSeq) {
		this.penSeq = penSeq;
	}

	public Integer getPenAtdSeq() {
		return penAtdSeq;
	}

	public void setPenAtdSeq(Integer penAtdSeq) {
		this.penAtdSeq = penAtdSeq;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public int getSizeListaPrescricoes() {
		return sizeListaPrescricoes;
	}

	public void setSizeListaPrescricoes(int sizeListaPrescricoes) {
		this.sizeListaPrescricoes = sizeListaPrescricoes;
	}

	public int getSizeListaItensPrescricao() {
		return sizeListaItensPrescricao;
	}

	public void setSizeListaItensPrescricao(int sizeListaItensPrescricao) {
		this.sizeListaItensPrescricao = sizeListaItensPrescricao;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public List<AghAtendimentos> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<AghAtendimentos> atendimentos) {
		this.atendimentos = atendimentos;
	}
	
	/**
	 * Utilizada pelos pages para navegação
	 * @return
	 */
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	/**
	 * Utilizada pelo page para navegação
	 * @return
	 */
	public String getVoltarPara2() {
		return voltarPara2;
	}

	public void setVoltarPara2(String voltarPara2) {
		this.voltarPara2 = voltarPara2;
	}	
	
	public Boolean getShowModalAtendimentos() {
		return showModalAtendimentos;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		try {
			this.prescricaoMedica = prescricaoMedica;
			if(prescricaoMedica != null){
				this.carregarItens();
			}else{
				limparCampos();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Instance<CodPacienteFoneticaVO> getCodPacienteFonetica() {
		return codPacienteFonetica;
	}

	public void setCodPacienteFonetica(
			Instance<CodPacienteFoneticaVO> codPacienteFonetica) {
		this.codPacienteFonetica = codPacienteFonetica;
	}

	public void setListaPrescricoes(List<MpmPrescricaoMedica> listaPrescricoes) {
		this.listaPrescricoes = listaPrescricoes;
	}

	public void setShowModalAtendimentos(Boolean showModalAtendimentos) {
		this.showModalAtendimentos = showModalAtendimentos;
	}
	
}
