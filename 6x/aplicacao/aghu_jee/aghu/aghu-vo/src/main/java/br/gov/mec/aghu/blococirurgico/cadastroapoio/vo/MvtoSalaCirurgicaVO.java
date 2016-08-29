package br.gov.mec.aghu.blococirurgico.cadastroapoio.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class MvtoSalaCirurgicaVO {
	
	private Date dtInicioMvto;
	private Date dtFimMvto;
	private String nome;
	private String motivoInat;
	private DominioSituacao situacao;
	private TipoSalaVO tipoSala;
	private Boolean visivelMonitor;
	private String criadoEm;
	private String alteradoEm;
	
	public MvtoSalaCirurgicaVO(Date dtInicioMvto, Date dtFimMvto, String nome,
			String motivoInat, DominioSituacao situacao,
            TipoSalaVO tipoSala, Boolean visivelMonitor,
			String criadoEm, String alteradoEm) {
		super();
		this.dtInicioMvto = dtInicioMvto;
		this.dtFimMvto = dtFimMvto;
		this.nome = nome;
		this.motivoInat = motivoInat;
		this.situacao = situacao;
		this.tipoSala = tipoSala;
		this.visivelMonitor = visivelMonitor;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}
	public Date getDtInicioMvto() {
		return dtInicioMvto;
	}
	public void setDtInicioMvto(Date dtInicioMvto) {
		this.dtInicioMvto = dtInicioMvto;
	}
	public Date getDtFimMvto() {
		return dtFimMvto;
	}
	public void setDtFimMvto(Date dtFimMvto) {
		this.dtFimMvto = dtFimMvto;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getMotivoInat() {
		return motivoInat;
	}
	public void setMotivoInat(String motivoInat) {
		this.motivoInat = motivoInat;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	public Boolean getVisivelMonitor() {
		return visivelMonitor;
	}
	public void setVisivelMonitor(Boolean visivelMonitor) {
		this.visivelMonitor = visivelMonitor;
	}
	public String getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getAlteradoEm() {
		return alteradoEm;
	}
	public void setAlteradoEm(String alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	public TipoSalaVO getTipoSala() {
		return tipoSala;
	}
	public void setTipoSala(TipoSalaVO tipoSala) {
		this.tipoSala = tipoSala;
	}
}
