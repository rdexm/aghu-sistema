package br.gov.mec.aghu.paciente.prontuarioonline.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioAvaliacaoInterconsulta;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ExibirConsultoriasAmbulatoriaisRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ExibirConsultoriasAmbulatoriaisRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private static final long serialVersionUID = 2396947667999803305L;
	
	/**
	 * @ORADB: Procedure p_monta_resposta 
	 * @author Cristiano Alexandre Moretti
	 * @since 04/04/2012
	 */
	public String pMontaResposta(MamInterconsultas interconsulta) {
		String vTexto = "";     
		if(interconsulta.getConsultaMarcada() != null) {
			AacConsultas consulta = interconsulta.getConsultaMarcada(); 
			vTexto = "Consulta em " + DateUtil.dataToString(consulta.getDtConsulta(),"dd/MM/yyy HH:mm")
				+ " , " + getAmbulatorioFacade().obterDescricaoCidCapitalizada(consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getUnidadeFuncional().getDescricao(),CapitalizeEnum.TODAS) 
				+ " , Sala " + consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala()
				+ " , " + getAmbulatorioFacade().obterDescricaoCidCapitalizada(consulta.getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade(),CapitalizeEnum.TODAS)
				+ " , Equipe " + getAmbulatorioFacade().obterDescricaoCidCapitalizada(consulta.getGradeAgendamenConsulta().getEquipe().getNome(),CapitalizeEnum.TODAS); 
			if (consulta != null &&
					consulta.getGradeAgendamenConsulta() != null &&
					consulta.getGradeAgendamenConsulta().getProfServidor() != null &&
					consulta.getGradeAgendamenConsulta().getProfServidor().getPessoaFisica() != null &&
					consulta.getGradeAgendamenConsulta().getProfServidor().getPessoaFisica().getNome() != null) {
				String vTextoAnterior = vTexto;
				vTexto = vTextoAnterior + " , " + getAmbulatorioFacade().obterDescricaoCidCapitalizada(consulta.getGradeAgendamenConsulta().getProfServidor().getPessoaFisica().getNome(),CapitalizeEnum.TODAS);
			}
			// então... o obterDescricaoCidCapitalizada() é na verdade a implementação do mpmc_minusculo() do banco...
			// porque o programador que implementou esta função especificou seu nome apenas para capitalização de descrição de CID
			// sendo esta função genérica eu não sei =P... mas é a mesma função segundo a anotação ORADB dela...
		} else if (interconsulta.getAvaliacao() == DominioAvaliacaoInterconsulta.S || 
				interconsulta.getAvaliacao() == DominioAvaliacaoInterconsulta.I ||
				interconsulta.getAvaliacao() == DominioAvaliacaoInterconsulta.C) {
			vTexto = interconsulta.getParecerConsultor(); 
		}
		if(interconsulta.getDthrAvaliacao() != null) {
			String vTextoAnterior = vTexto; 
			vTexto = vTextoAnterior + "<br/>"
				+ "Em " + DateUtil.dataToString(interconsulta.getDthrAvaliacao(),"dd/MM/yyy HH:mm")
				+ " por " + getAmbulatorioFacade().obterDescricaoCidCapitalizada(interconsulta.getServidorAvalia().getPessoaFisica().getNome(),CapitalizeEnum.TODAS); 
		}
		return vTexto;  
	}


	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
}
