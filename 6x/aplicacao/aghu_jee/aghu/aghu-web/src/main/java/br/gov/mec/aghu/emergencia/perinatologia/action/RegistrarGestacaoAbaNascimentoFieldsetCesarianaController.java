package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoSelecionadoVO;
import br.gov.mec.aghu.perinatologia.vo.IndicacaoPartoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.utils.StringUtil;

public class RegistrarGestacaoAbaNascimentoFieldsetCesarianaController extends ActionController {
	private static final long serialVersionUID = -5897465478124868524L;

	@EJB
	private IEmergenciaFacade emergenciaFacade;

	@Inject
	private RegistrarGestacaoAbaNascimentoController registrarGestacaoAbaNascimentoController;
	
	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private Integer seqp;

	// McoIndicacaoNascimento selecionada na suggestionbox
	private McoIndicacaoNascimento indicacaoNascimento;

	// Lista de McoIndicacaoNascimento
	private List<IndicacaoPartoVO> listaNascIndicacoes = new ArrayList<IndicacaoPartoVO>();
	
	private McoCesarianas mcoCesarianaExcluir;
	private List<McoNascIndicacoes> listaNascIndicacoesRemover = new ArrayList<McoNascIndicacoes>();

	// Utilizado para remover item da lista
	private IndicacaoPartoVO indicacaoPartoVORemover;
	
	private Integer idProximoElemento = 1;
	
	private DadosNascimentoSelecionadoVO dadosNascimentoSelecionado;
	private McoCesarianas mcoCesarianasOriginal;

	/* -------------------------------------------------------------------------------------- */

	/**
	 * Suggestion Indicação - PESQUISA
	 * 
	 * @param param
	 * @return
	 */
	public List<McoIndicacaoNascimento> pesquisarIndicacoes(String param) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarMcoIndicacaoNascimentoAtivosPorDescricaoOuSeq((String) param, 100),pesquisarIndicacoesCount(param));
	}

	/**
	 * Suggestion Indicação - COUNT
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarIndicacoesCount(String param) {
		return emergenciaFacade.pesquisarMcoIndicacaoNascimentoAtivosPorDescricaoOuSeqCount((String) param);
	}

	/**
	 * Botão ADICIONAR
	 */
	public void adicionarIndicacao() {
		this.houveAlteracao();
		if (indicacaoNascimento != null) {
			McoNascIndicacoes mcoNascIndicacoes = new McoNascIndicacoes();
			mcoNascIndicacoes.setIndicacaoNascimento(indicacaoNascimento);
			listaNascIndicacoes.add(new IndicacaoPartoVO(this.idProximoElemento++, mcoNascIndicacoes));
			indicacaoNascimento = null;
		}
	}

	/**
	 * Botão REMOVER
	 */
	public void removerIndicacao() {
		this.houveAlteracao();
		if (indicacaoPartoVORemover != null) {
			listaNascIndicacoes.remove(indicacaoPartoVORemover);
			if (indicacaoPartoVORemover.getMcoNascIndicacoes().getSeq() != null) {
				this.listaNascIndicacoesRemover.add(indicacaoPartoVORemover.getMcoNascIndicacoes());
			}
			indicacaoPartoVORemover = null;
		}
	}

	/* -------------------------------------------------------------------------------------- */

	/**
	 * Utilizado para inicializar o fieldset
	 */
	public void prepararFieldset(final Integer gsoPacCodigo, final Short gsoSeqp, final Integer seqp,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado, McoCesarianas mcoCesarianasOriginal) {
		this.limparFieldset();
		this.gsoPacCodigo = gsoPacCodigo;
		this.gsoSeqp = gsoSeqp;
		this.seqp = seqp;
		this.dadosNascimentoSelecionado = dadosNascimentoSelecionado;
		this.mcoCesarianasOriginal = mcoCesarianasOriginal;
		
		List<McoNascIndicacoes> original = emergenciaFacade.pesquisarIndicacoesCesariana(this.gsoPacCodigo, this.gsoSeqp, this.seqp);
		for (McoNascIndicacoes mcoNascIndicacoes : original) {
			listaNascIndicacoes.add(new IndicacaoPartoVO(this.idProximoElemento++, mcoNascIndicacoes));
		}
		if (this.dadosNascimentoSelecionado.getMcoCesariana().getDthrPrevInicio() != null) {
			this.atualizarDthrInicioProcedimento(false);
		}
		if (this.dadosNascimentoSelecionado.getHrDuracaoFormatado() != null) {
			this.atualizarTempoProcedimento(false);
		}
	}
	
	/**
	 * Utilizado para inicializar o fieldset
	 */
	public void limparFieldset() {
		listaNascIndicacoes.clear();
		this.idProximoElemento = 1;
		listaNascIndicacoesRemover.clear();
	}

	/**
	 * Recupera a lista de McoNascIndicacoes, pois o fieldset utiliza VO.
	 * 
	 * @return
	 */
	public List<McoNascIndicacoes> obterListaMcoNascIndicacoes() {
		List<McoNascIndicacoes> result = new ArrayList<McoNascIndicacoes>();
		for (IndicacaoPartoVO indicacaoPartoVO : listaNascIndicacoes) {
			result.add(indicacaoPartoVO.getMcoNascIndicacoes());
		}
		return result;
	}
	
	public void atualizarDthrInicioProcedimento(boolean alterado) {
		if (alterado) {
			this.houveAlteracao();
		}
		this.dadosNascimentoSelecionado.setDthrInicioProcedimento(this.dadosNascimentoSelecionado.getMcoCesariana().getDthrPrevInicio());
	}
	
	public void atualizarTempoProcedimento(boolean alterado) {
		if (alterado) {
			this.houveAlteracao();
		}
		this.dadosNascimentoSelecionado.setTempoProcedimento(this.dadosNascimentoSelecionado.getHrDuracaoFormatado());
		
		this.dadosNascimentoSelecionado.getMcoCesariana().setHrDuracao(this.obterHorarioFormatadoShort(
				this.dadosNascimentoSelecionado.getHrDuracaoFormatado()));
	}
	
	private Short obterHorarioFormatadoShort(String horario) {
		if (horario != null && !horario.isEmpty()
				&& horario.length() >= 4) {
			
			String[] arrayHorasMinutos = horario.split(":");
			
			String horas = StringUtil.adicionaZerosAEsquerda(arrayHorasMinutos[0], 2);
			String minutos = StringUtil.adicionaZerosAEsquerda(arrayHorasMinutos[1], 2);
			
			return Short.valueOf(horas.concat(minutos));
		}
		return null;
	}
	
	public void limparCampos() {
		this.houveAlteracao();
		this.limparFieldset();
		this.dadosNascimentoSelecionado.setHrDuracaoFormatado(null);
		List<McoNascIndicacoes> original = emergenciaFacade.pesquisarIndicacoesCesariana(this.gsoPacCodigo, this.gsoSeqp, this.seqp);
		// Deve remover todos os registros que estão no banco.
		listaNascIndicacoesRemover.addAll(original);
		// Limpa os campos da Cesariana.
		this.dadosNascimentoSelecionado.setMcoCesariana(new McoCesarianas());
		// Mantém o McoCesarianas na memória para excluir depois.
		if (this.mcoCesarianasOriginal.getId() != null) {
			this.mcoCesarianaExcluir = this.mcoCesarianasOriginal;
		}
	}
	
	public void houveAlteracao() {
		this.registrarGestacaoAbaNascimentoController.setHouveAlteracao(Boolean.TRUE);
	}

	/* -------------------------------------------------------------------------------------- */

	public boolean isPermExecutarIndicacoesAbaNascimento() {
		return registrarGestacaoAbaNascimentoController.isPermExecutarIndicacoesAbaNascimento();
	}

	/* -------------------------------------------------------------------------------------- */

	public Integer getGsoPacCodigo() {
		return gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public McoIndicacaoNascimento getIndicacaoNascimento() {
		return indicacaoNascimento;
	}

	public void setIndicacaoNascimento(McoIndicacaoNascimento indicacaoNascimento) {
		this.indicacaoNascimento = indicacaoNascimento;
	}

	public List<IndicacaoPartoVO> getListaNascIndicacoes() {
		return listaNascIndicacoes;
	}

	public void setListaNascIndicacoes(List<IndicacaoPartoVO> listaNascIndicacoes) {
		this.listaNascIndicacoes = listaNascIndicacoes;
	}

	public McoCesarianas getMcoCesarianaExcluir() {
		return mcoCesarianaExcluir;
	}

	public void setMcoCesarianaExcluir(McoCesarianas mcoCesarianaExcluir) {
		this.mcoCesarianaExcluir = mcoCesarianaExcluir;
	}

	public List<McoNascIndicacoes> getListaNascIndicacoesRemover() {
		return listaNascIndicacoesRemover;
	}

	public void setListaNascIndicacoesRemover(
			List<McoNascIndicacoes> listaNascIndicacoesRemover) {
		this.listaNascIndicacoesRemover = listaNascIndicacoesRemover;
	}

	public IndicacaoPartoVO getIndicacaoPartoVORemover() {
		return indicacaoPartoVORemover;
	}

	public void setIndicacaoPartoVORemover(IndicacaoPartoVO indicacaoPartoVORemover) {
		this.indicacaoPartoVORemover = indicacaoPartoVORemover;
	}

	public DadosNascimentoSelecionadoVO getDadosNascimentoSelecionado() {
		return dadosNascimentoSelecionado;
	}

	public void setDadosNascimentoSelecionado(
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) {
		this.dadosNascimentoSelecionado = dadosNascimentoSelecionado;
	}

	public McoCesarianas getMcoCesarianasOriginal() {
		return mcoCesarianasOriginal;
	}

	public void setMcoCesarianasOriginal(McoCesarianas mcoCesarianasOriginal) {
		this.mcoCesarianasOriginal = mcoCesarianasOriginal;
	}
}