package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import br.gov.mec.aghu.blococirurgico.vo.MbcEquipeVO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoNotificacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO de #41035 – Lista Pacientes CCIH
 * 
 * @author aghu
 *
 */

public class FiltroListaPacienteCCIHVO implements BaseBean {

	private static final long serialVersionUID = 3435243133370774473L;

	private Integer prontuario;
	private Integer consulta;
	private Date dtInicioAtendimento;
	private Date dtFimAtendimento;
	private Date dtInicioCriterios;
	private Date dtFimCriterios;

	// CIRURGIAS
	private MbcEquipeVO equipe;
	private AghUnidadesFuncionais unfCirurgia;

	// INTERNAÇÕES
	private DominioSimNao indInternado;
	private AghUnidadesFuncionais unfInternacao;
	private AinLeitos leito;

	// NOTIFICAÇÕES
	private boolean doencaCondicao;
	private boolean topografiaInfeccao;
	private boolean procedimentoRisco;
	private boolean fatoresPredisponente;
	private boolean gmr;
	private DominioSituacaoNotificacao situacaoNotificacao;
	private DominioConfirmacaoCCI conferido;

	private Integer codigoProcedimento;
	private Integer codigoDoencaCondicao;
	private Short codigoTopografia;

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getConsulta() {
		return consulta;
	}

	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}

	public Date getDtInicioAtendimento() {
		return dtInicioAtendimento;
	}

	public void setDtInicioAtendimento(Date dtInicioAtendimento) {
		this.dtInicioAtendimento = dtInicioAtendimento;
	}

	public Date getDtFimAtendimento() {
		return dtFimAtendimento;
	}

	public void setDtFimAtendimento(Date dtFimAtendimento) {
		this.dtFimAtendimento = dtFimAtendimento;
	}

	public MbcEquipeVO getEquipe() {
		return equipe;
	}

	public void setEquipe(MbcEquipeVO equipe) {
		this.equipe = equipe;
	}

	public AghUnidadesFuncionais getUnfCirurgia() {
		return unfCirurgia;
	}

	public void setUnfCirurgia(AghUnidadesFuncionais unfCirurgia) {
		this.unfCirurgia = unfCirurgia;
	}

	public DominioSimNao getIndInternado() {
		return indInternado;
	}

	public void setIndInternado(DominioSimNao indInternado) {
		this.indInternado = indInternado;
	}

	public AghUnidadesFuncionais getUnfInternacao() {
		return unfInternacao;
	}

	public void setUnfInternacao(AghUnidadesFuncionais unfInternacao) {
		this.unfInternacao = unfInternacao;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public boolean isDoencaCondicao() {
		return doencaCondicao;
	}

	public void setDoencaCondicao(boolean doencaCondicao) {
		this.doencaCondicao = doencaCondicao;
	}

	public boolean isTopografiaInfeccao() {
		return topografiaInfeccao;
	}

	public void setTopografiaInfeccao(boolean topografiaInfeccao) {
		this.topografiaInfeccao = topografiaInfeccao;
	}

	public boolean isProcedimentoRisco() {
		return procedimentoRisco;
	}

	public void setProcedimentoRisco(boolean procedimentoRisco) {
		this.procedimentoRisco = procedimentoRisco;
	}

	public boolean isFatoresPredisponente() {
		return fatoresPredisponente;
	}

	public void setFatoresPredisponente(boolean fatoresPredisponente) {
		this.fatoresPredisponente = fatoresPredisponente;
	}

	public boolean isGmr() {
		return gmr;
	}

	public void setGmr(boolean gmr) {
		this.gmr = gmr;
	}

	public DominioSituacaoNotificacao getSituacaoNotificacao() {
		return situacaoNotificacao;
	}

	public void setSituacaoNotificacao(DominioSituacaoNotificacao situacaoNotificacao) {
		this.situacaoNotificacao = situacaoNotificacao;
	}

	public DominioConfirmacaoCCI getConferido() {
		return conferido;
	}

	public void setConferido(DominioConfirmacaoCCI conferido) {
		this.conferido = conferido;
	}

	public Date getDtInicioCriterios() {
		return dtInicioCriterios;
	}

	public void setDtInicioCriterios(Date dtInicioCriterios) {
		this.dtInicioCriterios = dtInicioCriterios;
	}

	public Date getDtFimCriterios() {
		return dtFimCriterios;
	}

	public void setDtFimCriterios(Date dtFimCriterios) {
		this.dtFimCriterios = dtFimCriterios;
	}

	public Integer getCodigoProcedimento() {
		return codigoProcedimento;
	}

	public void setCodigoProcedimento(Integer codigoProcedimento) {
		this.codigoProcedimento = codigoProcedimento;
	}

	public Integer getCodigoDoencaCondicao() {
		return codigoDoencaCondicao;
	}

	public void setCodigoDoencaCondicao(Integer codigoDoencaCondicao) {
		this.codigoDoencaCondicao = codigoDoencaCondicao;
	}

	public Short getCodigoTopografia() {
		return codigoTopografia;
	}

	public void setCodigoTopografia(Short codigoTopografia) {
		this.codigoTopografia = codigoTopografia;
	}

}
