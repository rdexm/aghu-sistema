package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;

public class FatProcedAmbRealizadoBuilder {
	
	private Date dthrRealizado;
	private DominioSituacaoProcedimentoAmbulatorio situacao;
	private DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca;
	private Short quantidade;
	private AghUnidadesFuncionais unidadeFuncional;
	private AghEspecialidades especialidade;
	private AipPacientes paciente;
	private DominioOrigemProcedimentoAmbulatorialRealizado indOrigem;
	private AghAtendimentos atendimento;
	private FatConvenioSaudePlano convenioSaudePlano;
	private Integer ppcCrgSeq;
	private String alteradoPor;
	private String criadoPor;
	private Date criadoEm;
	private RapServidores servidor;
	private FatCompetencia fatCompetencia;
	private RapServidores servidorResponsavel;
	private FatProcedHospInternos procedimentoHospitalarInterno;
	
	public FatProcedAmbRealizado builder(){
		FatProcedAmbRealizado fatProcedAmbRealizado = new FatProcedAmbRealizado(); 
		
		fatProcedAmbRealizado.setDthrRealizado(dthrRealizado);
		fatProcedAmbRealizado.setSituacao(situacao);
		fatProcedAmbRealizado.setLocalCobranca(localCobranca);
		fatProcedAmbRealizado.setQuantidade(quantidade);
		fatProcedAmbRealizado.setUnidadeFuncional(unidadeFuncional);
		fatProcedAmbRealizado.setEspecialidade(especialidade);
		fatProcedAmbRealizado.setPaciente(paciente);
		fatProcedAmbRealizado.setIndOrigem(indOrigem);
		fatProcedAmbRealizado.setAtendimento(atendimento);
		fatProcedAmbRealizado.setConvenioSaudePlano(convenioSaudePlano);
		fatProcedAmbRealizado.setPpcCrgSeq(ppcCrgSeq);
		fatProcedAmbRealizado.setAlteradoPor(alteradoPor);
		fatProcedAmbRealizado.setCriadoPor(criadoPor);
		fatProcedAmbRealizado.setCriadoEm(criadoEm);
		fatProcedAmbRealizado.setServidor(servidor);
		fatProcedAmbRealizado.setFatCompetencia(fatCompetencia);
		fatProcedAmbRealizado.setServidorResponsavel(servidorResponsavel);
		fatProcedAmbRealizado.setProcedimentoHospitalarInterno(procedimentoHospitalarInterno);
		
		return fatProcedAmbRealizado;
	}
		
	public FatProcedAmbRealizadoBuilder withDthrRealizado(Date dthrRealizado){
		this.dthrRealizado= dthrRealizado;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withSituacao(DominioSituacaoProcedimentoAmbulatorio situacao){
		this.situacao =situacao;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca){
		this.localCobranca = localCobranca;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withQuantidade(Short quantidade){
		this.quantidade = quantidade;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional){
		this.unidadeFuncional = unidadeFuncional;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withEspecialidade(AghEspecialidades especialidade){
		this.especialidade = especialidade;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withPaciente(AipPacientes paciente){
		this.paciente = paciente;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withIndOrigem(DominioOrigemProcedimentoAmbulatorialRealizado indOrigem){
		this.indOrigem = indOrigem;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withAtendimento(AghAtendimentos atendimento){
		this.atendimento = atendimento;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano){
		this.convenioSaudePlano = convenioSaudePlano;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withPpcCrgSeq(Integer ppcCrgSeq){
		this.ppcCrgSeq = ppcCrgSeq;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withAlteradoPor(String alteradoPor){
		this.alteradoPor = alteradoPor;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withCriadoPor(String criadoPor){
		this.criadoPor = criadoPor;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withCriadoEm(Date criadoEm){
		this.criadoEm = criadoEm;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withServidor(RapServidores servidor){
		this.servidor = servidor;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withFatCompetencia(FatCompetencia fatCompetencia){
		this.fatCompetencia = fatCompetencia;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withServidorResponsavel(RapServidores servidorResponsavel){
		this.servidorResponsavel = servidorResponsavel;
		return this;
	}
	
	public FatProcedAmbRealizadoBuilder withProcedimentoHospitalarInterno(FatProcedHospInternos procedimentoHospitalarInterno){
		this.procedimentoHospitalarInterno = procedimentoHospitalarInterno;
		return this;
	}

}
