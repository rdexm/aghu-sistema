package br.gov.mec.aghu.emergencia.perinatologia.action;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoSelecionadoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.utils.StringUtil;

public class RegistrarGestacaoAbaNascimentoFieldsetPartoController extends ActionController {
	private static final long serialVersionUID = -5897465478124868524L;

	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private RegistrarGestacaoAbaNascimentoController registrarGestacaoAbaNascimentoController;

	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	
	// Nascimento selecionado na grade de nascimentos
	private DadosNascimentoVO nascimento;
	// Dados do nascimento que popula os fieldsets
	private DadosNascimentoSelecionadoVO dadosNascimentoSelecionado;
	
	private boolean devePintarDuracaoTotal;

	
	public void prepararFieldset(final Integer gsoPacCodigo, final Short gsoSeqp, DadosNascimentoVO nascimento,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) {
		this.gsoPacCodigo = gsoPacCodigo;
		this.gsoSeqp = gsoSeqp;
		this.nascimento = nascimento;
		this.dadosNascimentoSelecionado = dadosNascimentoSelecionado;
		this.calcularDuracaoTotalParto(false);
		this.ajustarEpisot(false);
	}
	
	public void calcularDuracaoTotalParto(boolean alteracao) {
		if (alteracao) {
			this.houveAlteracao();
		}
		this.devePintarDuracaoTotal = this.emergenciaFacade
				.calcularDuracaoTotalParto(this.gsoPacCodigo, gsoSeqp, this.nascimento.getPeriodoExpulsivo(),
						this.nascimento.getPeriodoDilatacao(), this.dadosNascimentoSelecionado, this.nascimento);
		
		this.dadosNascimentoSelecionado.getMcoNascimento().setPeriodoExpulsivo(
				obterHorarioFormatadoShort(this.nascimento.getPeriodoExpulsivo()));
		
		this.dadosNascimentoSelecionado.getMcoNascimento().setPeriodoDilatacao(
				obterHorarioFormatadoShort(this.nascimento.getPeriodoDilatacao()));
	}
	
	public void ajustarEpisot(boolean alteracao) {
		if (alteracao) {
			this.houveAlteracao();
		}
		this.dadosNascimentoSelecionado.getMcoNascimento().setIndEpisotomia(this.nascimento.getIndEpisotomia());
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
	
	public void houveAlteracao() {
		this.registrarGestacaoAbaNascimentoController.setHouveAlteracao(Boolean.TRUE);
	}

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

	public DadosNascimentoVO getNascimento() {
		return nascimento;
	}

	public void setNascimento(DadosNascimentoVO nascimento) {
		this.nascimento = nascimento;
	}

	public DadosNascimentoSelecionadoVO getDadosNascimentoSelecionado() {
		return dadosNascimentoSelecionado;
	}

	public void setDadosNascimentoSelecionado(
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) {
		this.dadosNascimentoSelecionado = dadosNascimentoSelecionado;
	}

	public boolean isDevePintarDuracaoTotal() {
		return devePintarDuracaoTotal;
	}

	public void setDevePintarDuracaoTotal(boolean devePintarDuracaoTotal) {
		this.devePintarDuracaoTotal = devePintarDuracaoTotal;
	}
}