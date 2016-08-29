package br.gov.mec.aghu.exames.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioFichaTrabalhoAmostraON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioFichaTrabalhoAmostraON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@EJB
private IFaturamentoFacade faturamentoFacade;

@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IPacienteFacade pacienteFacade;

@Inject
private VAelSolicAtendsDAO vAelSolicAtendsDAO;

@EJB
private IParametroFacade parametroFacade;

	private static final long serialVersionUID = 4062266481609587577L;

	public RelatorioFichaTrabalhoPatologiaVO obterFichaTrabAmostra(Integer soeSeq, Short seqP, Short unfSeq) throws ApplicationBusinessException {
		RelatorioFichaTrabalhoPatologiaVO vo = getVAelSolicAtendsDAO().obterSolicitacaoAtendimentoFichaTrabAmostra(soeSeq, seqP);
		if(vo != null) {
			if(vo.getMatriculaResponsavel() != null && vo.getVinCodigoResponsavel() != null) {
				RapServidores resp = getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(new RapServidoresId(vo.getMatriculaResponsavel(), vo.getVinCodigoResponsavel()));
				vo.setNomeResponsavel(resp.getPessoaFisica().getNome());
			}
			//DADOS PACIENTE
			AelSolicitacaoExames sol = getAelSolicitacaoExameDAO().obterPorChavePrimaria(soeSeq);
			//CODIGO PACIENTE
			if(sol.getAtendimento() != null) {
				vo.setPacCodigo(sol.getAtendimento().getPaciente().getCodigo());
				//ORIGEM
				if(DominioOrigemAtendimento.N.equals(sol.getAtendimento().getOrigem())) {
					vo.setOrigem(DominioOrigemAtendimento.I.getDescricao());
				}
				else {
					vo.setOrigem(sol.getAtendimento().getOrigem().getDescricao());
				}
			}
			else {
				vo.setPacCodigo(sol.getAtendimentoDiverso().getAipPaciente() != null ? sol.getAtendimentoDiverso().getAipPaciente().getCodigo() : null);
				//ORIGEM
				if(sol.getAtendimentoDiverso() != null) {
					if(sol.getAtendimentoDiverso().getAbsCandidatosDoadores() != null && 
							sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getSeq() != null) {
						vo.setOrigem(DominioOrigemAtendimento.D.getDescricao());
					}
				}
				else {
					vo.setOrigem(null);
				}
			}
			//NOME PACIENTE
			if(vo.getPacCodigo() != null) {
				AipPacientes paciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(vo.getPacCodigo());
				vo.setNomePaciente(paciente.getNome());
				vo.setSexo(paciente.getSexo());
				vo.setProntuario(paciente.getProntuario());
				vo.setIdade(paciente.getIdadeAnoMesFormat());
				vo.setDtNascimento(paciente.getDtNascimento());
			}
			
			//CONVÊNIO - PLANO
			String convSaude = "";
			String convSaudePlan = "";
			if(vo.getCspSeq()!= null) {
				FatConvenioSaude convSau = getFaturamentoFacade().obterFatConvenioSaudePorId(vo.getCspCnvCodigo());
				convSaude = convSau.getDescricao();
			}
			if(vo.getCspCnvCodigo() != null && vo.getCspSeq() != null) {
				FatConvenioSaudePlano convSauPlan = getFaturamentoFacade().obterFatConvenioSaudePlano(vo.getCspCnvCodigo(), vo.getCspSeq().byteValue());
				convSaudePlan = convSauPlan.getDescricao();
			}
			vo.setConvenioPlano(convSaude + " / " + convSaudePlan);
			
			AghParametros exames = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SIGLAS_EXAMES_FICHA_AMOSTRA);
			vo.setListaExame(getAelItemSolicitacaoExameDAO().obterListaExamesFichaTrabAmo( soeSeq, seqP, unfSeq, exames.getVlrTexto()));
		}
		
		return vo;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected VAelSolicAtendsDAO getVAelSolicAtendsDAO() {
		return vAelSolicAtendsDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
}