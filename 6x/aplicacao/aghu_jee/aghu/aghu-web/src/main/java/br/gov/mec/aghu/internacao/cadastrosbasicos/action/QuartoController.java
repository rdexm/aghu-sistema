package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinCaracteristicaLeito;
import br.gov.mec.aghu.model.AinCaracteristicaLeitoId;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;



/**
 * Classe responsável por controlar as ações do criação e edição de quartos
 * 
 */

@SuppressWarnings("PMD.AghuTooManyMethods")
public class QuartoController  extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6152213850966982649L;

	@EJB
	private IAghuFacade aghuFacade;


	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private static final Enum[] INNER_JOINS = {AinQuartos.Fields.ACOMODACAO, AinQuartos.Fields.UNIDADES_FUNCIONAIS};
	private static final Enum[] LEFT_JOINS = {AinQuartos.Fields.CLINICA};
	private enum QuartoControllerExceptionCode implements BusinessExceptionCode {LEITO_EXISTENTE;}
	/**
	 * Quarto a ser associado/editada.
	 */
	private AinQuartos ainQuartos;
	private List<AinLeitosVO> leitosList = new ArrayList<AinLeitosVO>();

	/**
	 * Codigo do Quarto, obtido via page parameter.
	 */
	private Short quartoNumero;	
	
	/**
	 * Leito a ser associado/editada.
	 */
	private AinLeitos ainLeitos;
	
	private AinLeitosVO leitoVO;
	
	private String leitoID;
	
	private String cameFrom;
	
	private String idLeitoSemMovimento;
	
	private AinCaracteristicaLeito ainCaracteristicaLeito;		
	
	/**
	 * Característica do leito a ser associado/editada.
	 */
	private AinTipoCaracteristicaLeito ainTipoCaracteristicaLeito;
	
	
	private AghUnidadesFuncionaisVO aghUnidadesFuncionaisQuartoVO;	
	
	private AghUnidadesFuncionaisVO aghUnidadesFuncionaisLeitoVO;	
	
	private List<AinCaracteristicaLeito> ainCaracteristicas;
	
	private boolean edicao = false;
	
	private boolean edicaoLeito = false;
	
	private Boolean leitoPossuiMovimentacao = true;	
	
	private boolean consulta = false;
	
	private String voltarPara = null;
	
	private final String PAGE_LIST_QUARTO = "quartoList";

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void initForm(){
		if (leitoID!=null){
			ainLeitos=cadastrosBasicosInternacaoFacade.obterLeitoPorId(leitoID, AinLeitos.Fields.ESPECIALIDADE);
		}
		if (quartoNumero!=null){
			ainQuartos=cadastrosBasicosInternacaoFacade.obterQuarto(quartoNumero, INNER_JOINS, LEFT_JOINS);
			if(ainQuartos.getUnidadeFuncional() != null) {
				this.aghUnidadesFuncionaisQuartoVO = cadastrosBasicosInternacaoFacade.obterUnidadeFuncionalVO(ainQuartos.getUnidadeFuncional().getSeq());
			}			
		}	
		if (leitosList.isEmpty()) {
			leitosList = cadastrosBasicosInternacaoFacade.pesquisaLeitosPorNroQuarto(quartoNumero);
		}
	}

	public void inicio() {
		if (quartoNumero!=null){
			initForm();
			edicao = true;
		} else  {
			limparTela();
		}
	}

	private void limparTela() {
		this.ainQuartos = new AinQuartos();
		this.ainQuartos.setConsClin(true);
		this.ainQuartos.setConsSexo(true);
		this.aghUnidadesFuncionaisQuartoVO = null;
		this.aghUnidadesFuncionaisLeitoVO = null;
		edicao = false;
		quartoNumero = null;
		leitosList=new ArrayList<>();
	}
	
	public void novoLeito() {
		this.ainLeitos = new AinLeitos();
		this.ainLeitos.setSituacao(true);
		this.ainLeitos.setConsClinUnidade(true);
		this.ainLeitos.setConsEsp(true);
		this.ainLeitos.setIndBloqLeitoLimpeza(true);
		this.ainLeitos.setPertenceRefl(true);
		this.ainCaracteristicas = new ArrayList<AinCaracteristicaLeito>();
		ainCaracteristicaLeito = new AinCaracteristicaLeito();
		this.aghUnidadesFuncionaisLeitoVO = null;
		leitoVO = new AinLeitosVO();
		edicaoLeito = false;
	}
	
	public void editarLeito(AinLeitosVO vo) {

		leitoVO = vo;
		
		if(vo.getAinLeito() == null){
			final Enum[] left_joins = {AinLeitos.Fields.ESPECIALIDADE};
			final Enum[] inner_joins = {AinLeitos.Fields.TIPO_MOVIMENTO_LEITO, AinLeitos.Fields.UNIDADE_FUNCIONAL};
			ainLeitos = cadastrosBasicosInternacaoFacade.obterLeitoPorId(vo.getLeitoID(), inner_joins, left_joins);
			vo.setAinLeito(ainLeitos);
		} else {
			ainLeitos = vo.getAinLeito();
		}
		
		if(ainLeitos.getUnidadeFuncional() != null) {
			this.aghUnidadesFuncionaisLeitoVO = this.cadastrosBasicosInternacaoFacade.popularUnidadeFuncionalVO(ainLeitos.getUnidadeFuncional());
		} else {
			this.aghUnidadesFuncionaisLeitoVO = null;
		}
		
		obterCaracteristicasDoLeito();
		ainCaracteristicaLeito = new AinCaracteristicaLeito();
		ainTipoCaracteristicaLeito = null;
			
		edicaoLeito = true;
	}

	private void obterCaracteristicasDoLeito() {
		this.ainCaracteristicas = new ArrayList<AinCaracteristicaLeito>();
		
		if(leitoVO.getAinCaracteristicas() == null){
			if(leitoVO.getCaracteristicasInseridas() != null && !leitoVO.getCaracteristicasInseridas().isEmpty()){
				ainCaracteristicas = leitoVO.getCaracteristicasInseridas();
				
			} else {
				List<AinCaracteristicaLeito> aux = cadastrosBasicosInternacaoFacade.obterCaracteristicasDoLeito(leitoVO.getLeitoID());
				if(aux != null){
					this.ainCaracteristicas.addAll(aux);
				}
				
				leitoVO.setAinCaracteristicas(ainCaracteristicas);
			}
			
		} else {
			ainCaracteristicas = leitoVO.getAinCaracteristicas();
		}
	}

	public void adicionarLeito() {
		try {

			cadastrosBasicosInternacaoFacade.validaCaracteristicaPrincipal(ainCaracteristicas);
				
			leitoVO.setLeito(ainLeitos.getLeito());
			if(ainLeitos.getEspecialidade() != null){
				leitoVO.setNomeEspecialidade(ainLeitos.getEspecialidade().getNomeEspecialidade());
			}
			leitoVO.setIndSituacao(ainLeitos.getIndSituacao());
			leitoVO.setIndConsClinUnidade(ainLeitos.getIndConsClinUnidade());
			leitoVO.setIndConsEsp(ainLeitos.getIndConsEsp());
			leitoVO.setIndBloqLeitoLimpeza(ainLeitos.getIndBloqLeitoLimpeza());
			leitoVO.setIndPertenceRefl(ainLeitos.getIndPertenceRefl());
			leitoVO.setQtCaracteristicas(ainCaracteristicas.size());
			leitoVO.setAinLeito(ainLeitos);
			
			if(!edicaoLeito) {
				//cadastrosBasicosInternacaoFacade.validarLeitoExistente(ainLeitos, ainQuartos);
				if (ainQuartos != null && ainQuartos.getNumero() != null) {
					cadastrosBasicosInternacaoFacade.validarLeitoExistente(leitoVO.getLeitoID(), ainQuartos);
				} else {
					this.validarLeitoIguais(leitoVO);
				}
				leitosList.add(leitoVO);
				leitoVO.setNovoLeito(true);
			}
			Collections.sort(leitosList, COMPARATOR_AINLEITOS);
			this.closeDialog("modalLeitosWG");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		if (quartoNumero!=null){
			initForm();
		}
	}
	
	private void validarLeitoIguais(AinLeitosVO pLeito) throws ApplicationBusinessException {
		for (AinLeitosVO leito : leitosList) {
			if (leito.getLeito().equalsIgnoreCase(pLeito.getLeito())) {
				throw new ApplicationBusinessException(QuartoControllerExceptionCode.LEITO_EXISTENTE);
			}
		}
	}
	
	public void carregarCaracteristicas(AinLeitosVO vo){
		ainLeitos = vo.getAinLeito();
		leitoVO = vo;
		obterCaracteristicasDoLeito();
	}
	
	public void adicionarCaracteristica() {
		AinCaracteristicaLeitoId id = new AinCaracteristicaLeitoId(ainLeitos.getLeitoID(), ainTipoCaracteristicaLeito.getCodigo());
		
		if(ainTipoCaracteristicaLeito != null && ainCaracteristicas.indexOf(new AinCaracteristicaLeito(id, null, null)) < 0) {
			ainCaracteristicaLeito.setId(id);
			ainCaracteristicaLeito.setLeito(ainLeitos);
			ainCaracteristicaLeito.setTipoCaracteristicaLeito(ainTipoCaracteristicaLeito);
			ainCaracteristicas.add(ainCaracteristicaLeito);
			
			leitoVO.getCaracteristicasInseridas().add(ainCaracteristicaLeito);
		} else {
			apresentarMsgNegocio(Severity.ERROR, "CARACTERISTICA_DUPLICADA");
		}
		
		ainCaracteristicaLeito = new AinCaracteristicaLeito();
		ainCaracteristicaLeito.setPrincipal(false);
		ainTipoCaracteristicaLeito = null;
	}
	
	public void removerCaracteristica(AinCaracteristicaLeito ainCaracteristicaLeito) {
		leitoVO.getCaracteristicasExcluidas().add(ainCaracteristicaLeito);
		leitoVO.getCaracteristicasInseridas().remove(ainCaracteristicaLeito);
		ainCaracteristicas.remove(ainCaracteristicaLeito);
	}
	
	private static final Comparator<AinLeitosVO> COMPARATOR_AINLEITOS= new Comparator<AinLeitosVO>() {
		@Override
		public int compare(AinLeitosVO o1, AinLeitosVO o2) {
			return o1.getLeito().toUpperCase().compareTo(
					o2.getLeito().toUpperCase());
		}
	};

	/**
	 * Método que realiza a ação do botão gravar na tela de cadastro de
	 * Quartos
	 */
	public String gravar() {
		try {			
			populateDadosGravar();
			if(!this.edicao) {
				cadastrosBasicosInternacaoFacade.validarQuartoExistente(ainQuartos.getDescricao());
			}
			cadastrosBasicosInternacaoFacade.persistirQuarto(ainQuartos, leitosList);
			if (!this.edicao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_QUARTO", this.ainQuartos.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_QUARTO", this.ainQuartos.getDescricao());
			}
		    limparTela();
		 }catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
			return null;
		 }
		 return PAGE_LIST_QUARTO;
   }

   
   public void gravarLeitos() {
	   try {
		   populateDadosGravar();
		   cadastrosBasicosInternacaoFacade.persistirQuarto(ainQuartos, leitosList);
	   } catch (ApplicationBusinessException e) {
		   apresentarExcecaoNegocio(e);
	   }
	   inicio();
   }
   
   public void populateDadosGravar() {
	   if(aghUnidadesFuncionaisQuartoVO != null && aghUnidadesFuncionaisQuartoVO.getSeq() != null) {
		   ainQuartos.setUnidadeFuncional(this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(aghUnidadesFuncionaisQuartoVO.getSeq()));
	   }
	   if(aghUnidadesFuncionaisLeitoVO != null && aghUnidadesFuncionaisLeitoVO.getSeq() != null) {
	       ainLeitos.setUnidadeFuncional(this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(aghUnidadesFuncionaisLeitoVO.getSeq()));
	   }	   
   }
	
   public List<AinLeitosVO>  pesquisaLeitosPorNroQuarto(Short nroQuarto ){
		return this.cadastrosBasicosInternacaoFacade.pesquisaLeitosPorNroQuarto(nroQuarto);
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Observação alta paciente
	 */
	public String cancelar() {	
		limparTela();
		
		if (!StringUtils.isBlank(voltarPara)) {
			return voltarPara;
		}

		return PAGE_LIST_QUARTO;
	}
	
	public void recuperarIdLeitoSemMovimento(AinLeitosVO leito){
		this.idLeitoSemMovimento = leito.getLeitoID();
	}
	
	public void excluirLeitoSemMovimentacao(AinLeitosVO leitoVo) {
		try {
			AinLeitos leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(leitoVo.getLeitoID());
			if (leito != null) {
				excluiLeitoBase(leito,leitoVo);
			}
			excluiLeitoMemoria(leitoVo);
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_LEITO_SEM_MOVIMENTACAO_EXCLUIDO");
			
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_LEITO_SEM_MOVIMENTACAO_EXCLUIDO");
		}
	}
	
	private void excluiLeitoBase(AinLeitos leito,AinLeitosVO leitoVo) throws ApplicationBusinessException {
		leitoVo.setLeitoID(leito.getLeitoID());
		if (!leitoPossuiMovimentacao(leitoVo) && leito.getLeitoID() != null) {
			cadastrosBasicosInternacaoFacade.excluirLeitoSemMovimentacao(leito);
		}
	}

	private void excluiLeitoMemoria(AinLeitosVO leitoVo) {
		this.leitosList.remove(leitoVo);
	}
	
	public Boolean leitoPossuiMovimentacao(AinLeitosVO leito) {
		if (leito.getLeitoID() != null) {
			try {
				this.leitoPossuiMovimentacao = cadastrosBasicosInternacaoFacade.leitoPossuiMovimentacao(leito);
			} catch (ApplicationBusinessException e) {
				super.apresentarExcecaoNegocio(e);
			}
		} else {
			this.leitoPossuiMovimentacao = false;
		}
		return this.leitoPossuiMovimentacao;
	}
	
	public List<AghUnidadesFuncionaisVO> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(String parametro) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalVOPorCodigoEDescricao(parametro);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidadeSiglaEDescricao(String parametro) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarEspecialidadeSiglaEDescricao(parametro);
	}
	
	public List<AinAcomodacoes> pesquisarAcomodacoesPorCodigoOuDescricao(String parametro) {		
		return this.cadastrosBasicosInternacaoFacade.pesquisarAcomodacoesPorCodigoOuDescricao(parametro);
	}	
	
	public List<AinTipoCaracteristicaLeito> pesquisarTiposCaracteristicasPorCodigoOuDescricao(String parametro) {	
		return this.cadastrosBasicosInternacaoFacade.pesquisarTiposCaracteristicasPorCodigoOuDescricao(parametro);
	}

	public List<AghClinicas> pesquisarClinicas(String paramPesquisa) {
		return this.aghuFacade.pesquisarClinicas(paramPesquisa);
	}

	public AinQuartos getAinQuartos() {
		return ainQuartos;
	}

	public void setAinQuartos(AinQuartos ainQuartos) {
		this.ainQuartos = ainQuartos;
	}

	public AghUnidadesFuncionaisVO getAghUnidadesFuncionaisQuartoVO() {
		return aghUnidadesFuncionaisQuartoVO;
	}

	public void setAghUnidadesFuncionaisQuartoVO(
			AghUnidadesFuncionaisVO aghUnidadesFuncionaisQuartoVO) {
		this.aghUnidadesFuncionaisQuartoVO = aghUnidadesFuncionaisQuartoVO;
	}

	public AghUnidadesFuncionaisVO getAghUnidadesFuncionaisLeitoVO() {
		return aghUnidadesFuncionaisLeitoVO;
	}

	public void setAghUnidadesFuncionaisLeitoVO(
			AghUnidadesFuncionaisVO aghUnidadesFuncionaisLeitoVO) {
		this.aghUnidadesFuncionaisLeitoVO = aghUnidadesFuncionaisLeitoVO;
	}

	public AinLeitos getAinLeitos() {
		return ainLeitos;
	}

	public void setAinLeitos(AinLeitos ainLeitos) {
		this.ainLeitos = ainLeitos;
	}

	public AinTipoCaracteristicaLeito getAinTipoCaracteristicaLeito() {
		return ainTipoCaracteristicaLeito;
	}

	public void setAinTipoCaracteristicaLeito(
			AinTipoCaracteristicaLeito ainTipoCaracteristicaLeito) {
		this.ainTipoCaracteristicaLeito = ainTipoCaracteristicaLeito;
	}

	public AinCaracteristicaLeito getAinCaracteristicaLeito() {
		return ainCaracteristicaLeito;
	}

	public void setAinCaracteristicaLeito(
			AinCaracteristicaLeito ainCaracteristicaLeito) {
		this.ainCaracteristicaLeito = ainCaracteristicaLeito;
	}

	public List<AinCaracteristicaLeito> getAinCaracteristicas() {
		return ainCaracteristicas;
	}

	public void setAinCaracteristicas(
			List<AinCaracteristicaLeito> ainCaracteristicas) {
		this.ainCaracteristicas = ainCaracteristicas;
	}

	public boolean isEdicaoLeito() {
		return edicaoLeito;
	}

	public void setEdicaoLeito(boolean edicaoLeito) {
		this.edicaoLeito = edicaoLeito;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public void setConsulta(boolean consulta) {
		this.consulta = consulta;
	}

	public boolean isConsulta() {
		return consulta;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public AghEspecialidades getAinLeitosEspecialidade() {
		return ainLeitos == null ? null : ainLeitos.getEspecialidade();
	}

	public void setAinLeitosEspecialidade(
			AghEspecialidades ainLeitosEspecialidade) {
		if (ainLeitos != null) {
			ainLeitos.setEspecialidade(ainLeitosEspecialidade);
		}
	}

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Short getQuartoNumero() {
		return quartoNumero;
	}

	public void setQuartoNumero(Short quartoNumero) {
		this.quartoNumero = quartoNumero;
	}

	public List<AinLeitosVO> getLeitosList() {
		return leitosList;
	}

	public void setLeitosList(List<AinLeitosVO> leitosList) {
		this.leitosList = leitosList;
	} 

	public AinLeitosVO getLeitoVO() {
		return leitoVO;
	}

	public void setLeitoVO(AinLeitosVO leitoVO) {
		this.leitoVO = leitoVO;
	}

	public String getIdLeitoSemMovimento() {
		return idLeitoSemMovimento;
	}

	public void setIdLeitoSemMovimento(String idLeitoSemMovimento) {
		this.idLeitoSemMovimento = idLeitoSemMovimento;
	}
	
}