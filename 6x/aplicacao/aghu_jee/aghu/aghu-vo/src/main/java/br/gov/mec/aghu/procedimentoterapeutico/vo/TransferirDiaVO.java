package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurnoTodos;

/**
 * VO para Modal de Transferência de Dias em #44228 – Realizar Manutenção de Agendamento de Sessão Terapêutica
 * 
 * @author aghu
 *
 */
public class TransferirDiaVO {

	private Date dataTransferencia;
	private String restricaoDatas;
	private Date restricaoInicio;
	private Date restricaoFim;
	private DominioTurnoTodos turno;
	private DominioTipoLocal acomodacao;

	public TransferirDiaVO() {
		super();
		this.turno = DominioTurnoTodos.TODOS;
		this.acomodacao = DominioTipoLocal.T;
	}

	public Date getDataTransferencia() {
		return dataTransferencia;
	}

	public void setDataTransferencia(Date dataTransferencia) {
		this.dataTransferencia = dataTransferencia;
	}

	public String getRestricaoDatas() {
		return restricaoDatas;
	}

	public void setRestricaoDatas(String restricaoDatas) {
		this.restricaoDatas = restricaoDatas;
	}

	public Date getRestricaoInicio() {
		return restricaoInicio;
	}

	public void setRestricaoInicio(Date restricaoInicio) {
		this.restricaoInicio = restricaoInicio;
	}

	public Date getRestricaoFim() {
		return restricaoFim;
	}

	public void setRestricaoFim(Date restricaoFim) {
		this.restricaoFim = restricaoFim;
	}

	public DominioTurnoTodos getTurno() {
		return turno;
	}

	public void setTurno(DominioTurnoTodos turno) {
		this.turno = turno;
	}

	public DominioTipoLocal getAcomodacao() {
		return acomodacao;
	}

	public void setAcomodacao(DominioTipoLocal acomodacao) {
		this.acomodacao = acomodacao;
	}

}
