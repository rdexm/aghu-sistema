package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import br.gov.mec.aghu.dominio.DominioSituacaoOrdemDeAdministracao;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.EceOrdemDeAdministracao;
import br.gov.mec.aghu.model.EceOrdemDeAdministracaoHist;
import br.gov.mec.aghu.model.RapServidores;

public class OrdemDeAdministracaoVO {

	private Integer seq;
	private DominioTurno turno;
	private DominioSituacaoOrdemDeAdministracao situacao;
	private Date dataReferencia;
	private RapServidores servidor;
	private String leitoId;
	private EceOrdemDeAdministracaoHist ordemAdministracaoHist;
	private EceOrdemDeAdministracao ordemAdministracao;
	
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public DominioTurno getTurno() {
		return turno;
	}
	
	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}
	
	public DominioSituacaoOrdemDeAdministracao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacaoOrdemDeAdministracao situacao) {
		this.situacao = situacao;
	}
	
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	public EceOrdemDeAdministracaoHist getOrdemAdministracaoHist() {
		return ordemAdministracaoHist;
	}
	
	public void setOrdemAdministracaoHist(
			EceOrdemDeAdministracaoHist ordemAdministracaoHist) {
		this.ordemAdministracaoHist = ordemAdministracaoHist;
	}
	
	public EceOrdemDeAdministracao getOrdemAdministracao() {
		return ordemAdministracao;
	}
	
	public void setOrdemAdministracao(EceOrdemDeAdministracao ordemAdministracao) {
		this.ordemAdministracao = ordemAdministracao;
	}

	public Date getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(Date dataReferencia) {
		this.dataReferencia = dataReferencia;
	}
	
	public String getDia(){
		if (dataReferencia!=null){
			return formatDate(dataReferencia, "dd");
		}
		return null;
	}
	
	public String getMes(){
		if (dataReferencia!=null){
			return formatDate(dataReferencia, "MM");
		}
		return null;
	}
	
	public String getAno(){
		if (dataReferencia!=null){
			return formatDate(dataReferencia, "yyyy");
		}
		return null;
	}
	
	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}
	
	private String formatDate(Date date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.format(date);

	}
}
