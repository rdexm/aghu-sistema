package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.model.AipPacientesJn;

public class SituacaoPacienteVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3417873827718603527L;
	
	private AipPacientesJn aipPacienteJn;
	private String informadoPor;
	private String localSitAnt;
	private String alteradoPor;
	private Date dataAlteracao;
	
	public SituacaoPacienteVO(Integer prontuario, String nomePaciente,
			Date jnDateTime, Date dtNascimento, Integer cctCodigoCadastro,
			Integer cctCodigoRecadastro, Integer serMatriculaCadastro,
			Integer serMatriculaRecadastro, Short serVinCodigoCadastro,
			Short serVinCodigoRecadastro, String alteradoPor) {
		
		aipPacienteJn = new AipPacientesJn();
		dataAlteracao = jnDateTime;

		aipPacienteJn.setProntuario(prontuario);
		aipPacienteJn.setNomePaciente(nomePaciente);
		aipPacienteJn.setDtNascimento(dtNascimento);

		if (cctCodigoCadastro != null) { 
			aipPacienteJn.setCodigoCentroCustoCadastro(cctCodigoCadastro);
		}
		
		if (cctCodigoRecadastro != null) {
			aipPacienteJn.setCodigoCentroCustoRecadastro(cctCodigoRecadastro);
		}
		
		if (serMatriculaCadastro != null && serVinCodigoCadastro != null) {
			aipPacienteJn.setMatriculaServidorCadastro(serMatriculaCadastro);
			aipPacienteJn.setVinCodigoServidorCadastro(serVinCodigoCadastro);
		}
		
		if (serMatriculaRecadastro != null && serVinCodigoRecadastro != null) {
			aipPacienteJn.setMatriculaServidorRecadastro(serMatriculaRecadastro);
			aipPacienteJn.setVinCodigoServidorRecadastro(serVinCodigoRecadastro);
		}

		this.alteradoPor = alteradoPor;
	}

	public AipPacientesJn getAipPacienteJn() {
		return aipPacienteJn;
	}

	public void setAipPacienteJn(AipPacientesJn aipPacienteJn) {
		this.aipPacienteJn = aipPacienteJn;
	}

	public String getInformadoPor() {
		return informadoPor;
	}

	public void setInformadoPor(String informadoPor) {
		this.informadoPor = informadoPor;
	}

	public String getLocalSitAnt() {
		return localSitAnt;
	}

	public void setLocalSitAnt(String localSitAnt) {
		this.localSitAnt = localSitAnt;
	}

	public String getAlteradoPor() {
		return alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}
	
	public Date getDataAlteracao() {
		return dataAlteracao;
	}
	
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
	
//	protected AipPacientesJnDAO getAipPacientesJnDAO(){
//		return AGHUDAOFactory.getDAO(AipPacientesJnDAO.class);
//	}

}
