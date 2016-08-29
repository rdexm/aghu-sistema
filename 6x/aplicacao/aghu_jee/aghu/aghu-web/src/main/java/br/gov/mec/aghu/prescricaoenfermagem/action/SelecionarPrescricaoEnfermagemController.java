package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.prescricaoenfermagem.action.ManutencaoPrescricaoEnfermagemController.EnumTipoImpressaoEnfermagem;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoEnfermagemVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class SelecionarPrescricaoEnfermagemController extends ActionController {

	private static final String CONTROLEPACIENTE_LISTAR_PACIENTES_INTERNADOS = "controlepaciente-listarPacientesInternados";
	private static final String LISTA_PACIENTES_ENFERMAGEM 					 = "prescricaoenfermagem-listaPacientesEnfermagem";
	private static final String ELABORACAO_PRESCRICAO_ENFERMAGEM 			 = "prescricaoenfermagem-elaboracaoPrescricaoEnfermagem";
	private static final String PACIENTE_PESQUISA_PACIENTE_COMPONENTE 		 = "paciente-pesquisaPacienteComponente";

	

	private static final Log LOG = LogFactory.getLog(SelecionarPrescricaoEnfermagemController.class);

	private static final long serialVersionUID = -8686020092293856030L;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFoneticaVO;
	private Integer prontuario;

	private AghAtendimentos atendimento;
	
	private AipPacientes paciente;
	
	private Date dtReferencia;
	
	private Integer prescricaoEnfermagemSeq;
	
	private Integer itemPrescricaoId;
	
	private Integer indice = -1;
	
	private EpePrescricaoEnfermagem prescricaoEnfermagem;
	
	private ItemPrescricaoEnfermagemVO itemPrescricaoEnfermagem;
	
	private List<EpePrescricaoEnfermagem> listaPrescricoes = new ArrayList<EpePrescricaoEnfermagem>(0);
	
	private List<ItemPrescricaoEnfermagemVO> listaItensPrescricao = new ArrayList<ItemPrescricaoEnfermagemVO>();
	
	private String cameFrom;
	
	private String voltarPara;
	
	private Integer atdSeq;

	private Integer penSeq;
	
	private Integer penAtdSeq;
	
	private int idConversacaoAnterior;
	
	@Inject
	private RelatorioPrescricaoEnfermagemController relatorioPrescricaoEnfermagemController;
	
	public enum SelecionarPrescricaoEnfermagemControllerExceptionCode implements
			BusinessExceptionCode {
		AIP_PACIENTE_NAO_ENCONTRADO;
	}
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 
		CodPacienteFoneticaVO codPac = codPacienteFoneticaVO.get();
		if (codPac != null && codPac.getCodigo() > 0) { 
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(codPac.getCodigo());
			this.prontuario = paciente.getProntuario();
		}else if(this.prontuario != null){
			this.paciente = this.pacienteFacade.obterPacientePorProntuario(this.prontuario);
		}
		
		carregarPrescricoes();
		//this.selecionarPacienteConsultaEdicao();
	
	}
	
	public void limparCampos() {
		this.paciente = null;
		this.listaPrescricoes = new ArrayList<EpePrescricaoEnfermagem>(0);
		this.listaItensPrescricao = new ArrayList<ItemPrescricaoEnfermagemVO>();
		this.atendimento = null;
		this.prescricaoEnfermagemSeq = null;
		this.itemPrescricaoEnfermagem = null;
		this.itemPrescricaoId = 0;
		this.dtReferencia = null;
		this.indice = -1;
		this.prescricaoEnfermagem = null;
	}
	
	/*public void selecionarPacienteConsultaEdicao() {
		
		if (this.pacCodigoFonetica!=null){
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(this.pacCodigoFonetica);		
		}else if (this.paciente != null || this.pacCodigo != null || this.prontuario != null) {
			if (this.prontuario != null) {
				this.paciente = this.pacienteFacade.obterPacientePorProntuario(this.prontuario);
			} else if (this.pacCodigo != null) {
				this.paciente = this.pacienteFacade.obterPacientePorCodigo(this.pacCodigo);
			}
		}
		
		if (this.paciente != null) {
			this.pacCodigo = this.paciente.getCodigo();
			this.prontuario = this.paciente.getProntuario();
			this.pacNome = this.paciente.getNome();
			this.pacCodigoFonetica = null;
			
						
		}
	}
	*/
	public String redirecionarPesquisaFonetica() {
		return PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}
	
	public boolean verificarDataFimMaiorAtual(Date dthrFim){
		return DateUtil.validaDataMaior(dthrFim, new Date());
	}
	
	public void reimprimirPrescricaoEnfermagem(EpePrescricaoEnfermagemId prescricaoId) throws JRException, ApplicationBusinessException{
		
		//Utiliza buscarDadosCabecalhoPrescricaoEnfermagemUtilizadoPrescricaoMedicaVO pois pode colocar em uso a prescrição
		PrescricaoEnfermagemVO vo = this.prescricaoEnfermagemFacade.buscarDadosCabecalhoPrescricaoEnfermagemUtilizadoPrescricaoMedicaVO(prescricaoId);
		vo.setListaCuidadoVO(this.prescricaoEnfermagemFacade.buscarCuidadosPrescricaoEnfermagem(prescricaoId, false));
		
		if(vo.getPrescricaoEnfermagem().getSituacao() == DominioSituacaoPrescricao.U){
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NAO_PODE_REIMPRIMIR_PRESCRICAO_EM_USO");
		}
		else if(vo.getPrescricaoEnfermagem().getSituacao() == DominioSituacaoPrescricao.L && vo.getPrescricaoEnfermagem().getDthrInicioMvtoPendente() != null){
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NAO_PODE_REIMPRIMIR_PRESCRICAO_PENDENTE");
		}
		else{
			this.relatorioPrescricaoEnfermagemController.setTipoImpressao(EnumTipoImpressaoEnfermagem.IMPRESSAO);
			this.relatorioPrescricaoEnfermagemController.setPrescricaoEnfermagemVO(vo);
			this.relatorioPrescricaoEnfermagemController.directPrint();
		}
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			if (paciente != null) {
				carregarPrescricoes();
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
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
			EpePrescricaoEnfermagem prescricaoSelecionada = null;
			if(this.listaPrescricoes != null && !this.listaPrescricoes.isEmpty()) {
				if(dtReferencia != null) {
					prescricaoSelecionada = (EpePrescricaoEnfermagem)CollectionUtils.find(this.listaPrescricoes, new Predicate() {  
						public boolean evaluate(Object o) {  
							if(DateUtils.truncate(((EpePrescricaoEnfermagem)o).getDtReferencia(), Calendar.DATE).compareTo(dtReferencia) == 0) {								
								return true;  
							}
							return false;  
						}  
					});
				}
				if(prescricaoSelecionada != null) {
					this.prescricaoEnfermagem = prescricaoSelecionada;
					this.carregarItens();
					this.indice = this.listaPrescricoes.indexOf(prescricaoSelecionada);
					if(this.indice.equals(listaPrescricoes.size()-1)) {
						this.indice--;
					}
					return;
				}
				else {
					this.apresentarMsgNegocio(Severity.ERROR,
							"Nenhuma prescrição encontrada para data informada.");
				}
				this.indice = this.listaPrescricoes.indexOf(prescricaoEnfermagem);
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
			this.listaPrescricoes = null;
			this.listaItensPrescricao = null;
			
			this.dtReferencia = null;
			if(this.paciente != null) {
				this.atendimento = this.prescricaoEnfermagemFacade.obterUnicoAtendimentoAtualPorProntuario(this.paciente.getProntuario());
				if(this.atendimento != null && !this.atendimento.getPrescricoesEnfermagem().isEmpty()) {
					this.listaPrescricoes = 	new ArrayList<EpePrescricaoEnfermagem>(this.atendimento.getPrescricoesEnfermagem());
					final BeanComparator prescricaoSorter = new BeanComparator("dtReferencia", new ReverseComparator());
					Collections.sort(this.listaPrescricoes, prescricaoSorter);
					
					if (this.listaPrescricoes != null
							&& !this.listaPrescricoes.isEmpty()) {
						this.prescricaoEnfermagem = this.listaPrescricoes.get(0);					
						this.carregarItens();
					}
				}
				else {
					this.listaPrescricoes = new ArrayList<EpePrescricaoEnfermagem>(0);
					this.listaItensPrescricao = new ArrayList<ItemPrescricaoEnfermagemVO>();
					this.prescricaoEnfermagemSeq = null;
					this.itemPrescricaoEnfermagem = null;
					this.itemPrescricaoId = 0;
				}
			}
			else {
				this.listaPrescricoes = new ArrayList<EpePrescricaoEnfermagem>(0);
				this.listaItensPrescricao = new ArrayList<ItemPrescricaoEnfermagemVO>();
				this.prescricaoEnfermagemSeq = null;
				this.itemPrescricaoEnfermagem = null;
				this.itemPrescricaoId = 0;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void carregarItens() throws BaseException {
		
		this.prescricaoEnfermagemSeq = this.prescricaoEnfermagem.getId().getSeq();
		
		this.listaItensPrescricao = this.prescricaoEnfermagemFacade
				.buscarItensPrescricaoEnfermagem(this.prescricaoEnfermagem.getId(), true);

		this.itemPrescricaoId = 0;
		
		if(this.listaItensPrescricao != null && !this.listaItensPrescricao.isEmpty()){
			this.itemPrescricaoEnfermagem = this.listaItensPrescricao.get(0);
		}else {
			this.itemPrescricaoEnfermagem = null;
		}
	}
	
	/*public void selecionarPrescricao(EpePrescricaoEnfermagem prescricao) {
		try {
			this.prescricaoEnfermagem = prescricao;
			this.carregarItens();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}			
	}*/
	
	/*public void selecionarItemPrescricao(Integer index) {
		if(this.listaItensPrescricao != null && !this.listaItensPrescricao.isEmpty()){
			this.itemPrescricaoEnfermagem = this.listaItensPrescricao.get(index);
			this.itemPrescricaoId = index;
		}
	}*/
	
	public String voltar() {
		limparCampos();
		
		if(cameFrom != null){
			if(cameFrom.equalsIgnoreCase("listaPacientesEnfermagem")){
				return LISTA_PACIENTES_ENFERMAGEM;
			}
			else if(cameFrom.equalsIgnoreCase("elaboracaoPrescricaoEnfermagem")) {
				return ELABORACAO_PRESCRICAO_ENFERMAGEM;
			}
			else if(cameFrom.equalsIgnoreCase("listarPacientesInternados")) {
				return CONTROLEPACIENTE_LISTAR_PACIENTES_INTERNADOS;
			}
		}
		return this.cameFrom;
	}
	
	public String voltarManterPrescricaoEnfermagem(){
		return "prescricaoenfermagem-manterPrescricaoEnfermagem";
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

	public List<EpePrescricaoEnfermagem> getListaPrescricoes() {
		return listaPrescricoes;
	}

	public void setListaPrescricoes(List<EpePrescricaoEnfermagem> listaPrescricoes) {
		this.listaPrescricoes = listaPrescricoes;
	}

	public Integer getPrescricaoEnfermagemSeq() {
		return prescricaoEnfermagemSeq;
	}

	public void setPrescricaoEnfermagemSeq(Integer prescricaoEnfermagemSeq) {
		this.prescricaoEnfermagemSeq = prescricaoEnfermagemSeq;
	}

	public Integer getItemPrescricaoId() {
		return itemPrescricaoId;
	}

	public void setItemPrescricaoId(Integer itemPrescricaoId) {
		this.itemPrescricaoId = itemPrescricaoId;
	}

	public List<ItemPrescricaoEnfermagemVO> getListaItensPrescricao() {
		return listaItensPrescricao;
	}

	public void setListaItensPrescricao(
			List<ItemPrescricaoEnfermagemVO> listaItensPrescricao) {
		this.listaItensPrescricao = listaItensPrescricao;
	}

	public ItemPrescricaoEnfermagemVO getItemPrescricaoEnfermagem() {
		return itemPrescricaoEnfermagem;
	}

	public void setItemPrescricaoEnfermagem(ItemPrescricaoEnfermagemVO itemPrescricaoEnfermagem) {
		this.itemPrescricaoEnfermagem = itemPrescricaoEnfermagem;
	}

	public Integer getIndice() {
		return indice;
	}

	public void setIndice(Integer indice) {
		this.indice = indice;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
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
	
	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}
	

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	/**
	 * Utilizado no page para navegação entre as telas
	 * 
	 * @return
	 */
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Instance<CodPacienteFoneticaVO> getCodPacienteFoneticaVO() {
		return codPacienteFoneticaVO;
	}

	public void setCodPacienteFoneticaVO(
			Instance<CodPacienteFoneticaVO> codPacienteFoneticaVO) {
		this.codPacienteFoneticaVO = codPacienteFoneticaVO;
	}

	public EpePrescricaoEnfermagem getPrescricaoEnfermagem() {
		return prescricaoEnfermagem;
	}

	public void setPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) {
		try {
			this.prescricaoEnfermagem = prescricaoEnfermagem;
			if(this.prescricaoEnfermagem != null){
				this.carregarItens();
			}else{
				carregarItens();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
	}	
	
}
