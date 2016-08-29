package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;

/**
 * VO para integrações com Estória #41696 Agendamento
 * 
 * @author aghu
 *
 */
public class ParametrosAgendamentoSessoesVO {
	private MptTipoSessao tipoSessao;
	private AipPacientes paciente;
	private Integer prescricao;
	private DominioTipoLocal acomodacao;
	private MptSalas sala;

	private List<CadIntervaloTempoVO> listaHorarios = new ArrayList<CadIntervaloTempoVO>();

	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getPrescricao() {
		return prescricao;
	}

	public void setPrescricao(Integer prescricao) {
		this.prescricao = prescricao;
	}

	public MptSalas getSala() {
		return sala;
	}

	public void setSala(MptSalas sala) {
		this.sala = sala;
	}

	public DominioTipoLocal getAcomodacao() {
		return acomodacao;
	}

	public void setAcomodacao(DominioTipoLocal acomodacao) {
		this.acomodacao = acomodacao;
	}

	public List<CadIntervaloTempoVO> getListaHorarios() {
		return listaHorarios;
	}

	public void setListaHorarios(List<CadIntervaloTempoVO> listaHorarios) {
		this.listaHorarios = listaHorarios;
	}
}
