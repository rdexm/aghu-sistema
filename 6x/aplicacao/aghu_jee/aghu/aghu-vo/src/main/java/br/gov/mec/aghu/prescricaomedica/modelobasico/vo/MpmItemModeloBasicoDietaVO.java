package br.gov.mec.aghu.prescricaomedica.modelobasico.vo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;

public class MpmItemModeloBasicoDietaVO {
	
	private MpmItemModeloBasicoDietaId id;
	private MpmModeloBasicoDieta modeloBasicoDieta;
	private AnuTipoItemDieta tipoItemDieta;

	private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;
	private RapServidores servidor;
	private Date criadoEm;
	private BigDecimal quantidade;
	private Short frequencia;
	private Byte numeroVezes;
	
	//GETTERs e SETTERs
	
	public MpmItemModeloBasicoDietaId getId() {
		return id;
	}
	public void setId(MpmItemModeloBasicoDietaId id) {
		this.id = id;
	}
	public MpmModeloBasicoDieta getModeloBasicoDieta() {
		return modeloBasicoDieta;
	}
	public void setModeloBasicoDieta(MpmModeloBasicoDieta modeloBasicoDieta) {
		this.modeloBasicoDieta = modeloBasicoDieta;
	}
	public AnuTipoItemDieta getTipoItemDieta() {
		return tipoItemDieta;
	}
	public void setTipoItemDieta(AnuTipoItemDieta tipoItemDieta) {
		this.tipoItemDieta = tipoItemDieta;
	}
	public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
		return tipoFrequenciaAprazamento;
	}
	public void setTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}
	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public BigDecimal getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}
	public Short getFrequencia() {
		return frequencia;
	}
	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}
	public Byte getNumeroVezes() {
		return numeroVezes;
	}
	public void setNumeroVezes(Byte numeroVezes) {
		this.numeroVezes = numeroVezes;
	}
	
	public String getDescricaoEditada() {
		StringBuffer sintaxeDieta = new StringBuffer(30);
		String descTfq;

		sintaxeDieta.append(this.tipoItemDieta.getDescricao());
		if (this.quantidade != null) {
			// Formatar quantidade 'fm999990D99'
			NumberFormat formatter = new DecimalFormat("######0.##");
			String qtdF = null; //

			if ((this.getTipoItemDieta() != null)
					&& (this.getTipoItemDieta().getUnidadeMedidaMedica() != null)
					&& (this.getTipoItemDieta().getUnidadeMedidaMedica()
							.getDescricao() != null)) {
				qtdF = formatter.format(this.quantidade)
						+ " "
						+ this.getTipoItemDieta().getUnidadeMedidaMedica()
								.getDescricao() ;
			} else {
				qtdF = formatter.format(this.quantidade);
			}

			sintaxeDieta.append(" " + qtdF + " ");
			
		} else {
			sintaxeDieta.append(' ');
		}

		// if (this.getTipoItemDieta() != null) {
		if (this.getTipoFrequenciaAprazamento() != null) {
			if (this.getTipoFrequenciaAprazamento().getSintaxe() != null) {

				descTfq = this.getTipoFrequenciaAprazamento().getSintaxe()
						.replace("#", this.getFrequencia().toString());
				sintaxeDieta.append(descTfq);

			} else if (this.getTipoFrequenciaAprazamento().getDescricao() != null) {
				descTfq = this.getTipoFrequenciaAprazamento().getDescricao();
				sintaxeDieta.append(descTfq);
			}
		}
		// }

		if (this.getNumeroVezes() != null) {
			sintaxeDieta.append(", número de vezes: ");
			sintaxeDieta.append(this.getNumeroVezes());

		}

		sintaxeDieta.append(" ; ");

		return sintaxeDieta.toString();

	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}
	
}
