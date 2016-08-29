package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioFormaRupturaBolsaRota;
import br.gov.mec.aghu.dominio.DominioLiquidoAmniotico;

public class DadosBolsaRotasVO implements Serializable{

	private static final long serialVersionUID = -2188921480405009026L;
	private DominioFormaRupturaBolsaRota formaRuptura;
	private Date dataHoraRompimento;
	private DominioLiquidoAmniotico liquidoAmniotico;
	private Boolean indDataHoraIgnorada;
	private boolean indOdorFetido;
	private boolean indOdorFetidoChkBox;
	
	public DominioFormaRupturaBolsaRota getFormaRuptura() {
		return formaRuptura;
	}
	public void setFormaRuptura(DominioFormaRupturaBolsaRota formaRuptura) {
		this.formaRuptura = formaRuptura;
	}
	public Date getDataHoraRompimento() {
		return dataHoraRompimento;
	}
	public void setDataHoraRompimento(Date dataHoraRompimento) {
		this.dataHoraRompimento = dataHoraRompimento;
	}
	public DominioLiquidoAmniotico getLiquidoAmniotico() {
		return liquidoAmniotico;
	}
	public void setLiquidoAmniotico(DominioLiquidoAmniotico liquidoAmniotico) {
		this.liquidoAmniotico = liquidoAmniotico;
	}
	public Boolean getIndDataHoraIgnorada() {
		return indDataHoraIgnorada;
	}
	public void setIndDataHoraIgnorada(Boolean indDataHoraIgnorada) {
		this.indDataHoraIgnorada = indDataHoraIgnorada;
	}
	public boolean isIndOdorFetido() {
		return indOdorFetido;
	}
	public void setIndOdorFetido(boolean indOdorFetido) {
		this.indOdorFetido = indOdorFetido;
	}
	public boolean isIndOdorFetidoChkBox() {
		return indOdorFetidoChkBox;
	}
	public void setIndOdorFetidoChkBox(boolean indOdorFetidoChkBox) {
		this.indOdorFetidoChkBox = indOdorFetidoChkBox;
	}
	
}
