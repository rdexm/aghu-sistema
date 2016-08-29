package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class AgendamentoProcedimentoCirurgicoVO implements Serializable {

	private static final long serialVersionUID = 1577711154884095924L;
	
	private String mensagem;
	private boolean mostrarModal;
	private boolean confirmarEscalaMedica;
	private Object parametro;

	public AgendamentoProcedimentoCirurgicoVO(String mensagem,
			boolean mostrarModal, boolean confirmarEscalaMedica, Object parametro) {
		super();
		this.mensagem = mensagem;
		this.mostrarModal = mostrarModal;
		this.confirmarEscalaMedica = confirmarEscalaMedica;
		this.parametro = parametro;
	}
	
	public AgendamentoProcedimentoCirurgicoVO() {
		this.mensagem = "";
		this.mostrarModal = false;
		this.confirmarEscalaMedica = true;
		this.parametro = null;
	}
	
	public Object getParametro() {
		return parametro;
	}
	
	public void setParametro(Object parametro) {
		this.parametro = parametro;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public boolean isMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

	public boolean isConfirmarEscalaMedica() {
		return confirmarEscalaMedica;
	}

	public void setConfirmarEscalaMedica(boolean confirmarEscalaMedica) {
		this.confirmarEscalaMedica = confirmarEscalaMedica;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (confirmarEscalaMedica ? 1231 : 1237);
		result = prime * result
				+ ((mensagem == null) ? 0 : mensagem.hashCode());
		result = prime * result + (mostrarModal ? 1231 : 1237);
		result = prime * result
				+ ((parametro == null) ? 0 : parametro.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AgendamentoProcedimentoCirurgicoVO)) {
			return false;
		}
		AgendamentoProcedimentoCirurgicoVO other = (AgendamentoProcedimentoCirurgicoVO) obj;
		if (confirmarEscalaMedica != other.confirmarEscalaMedica) {
			return false;
		}
		if (mensagem == null) {
			if (other.mensagem != null) {
				return false;
			}
		} else if (!mensagem.equals(other.mensagem)) {
			return false;
		}
		if (mostrarModal != other.mostrarModal) {
			return false;
		}
		if (parametro == null) {
			if (other.parametro != null) {
				return false;
			}
		} else if (!parametro.equals(other.parametro)) {
			return false;
		}
		return true;
	}

}
