package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FaturarCirurgiasCanceladasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(FaturarCirurgiasCanceladasRN.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 490494906798145240L;
	
	@Inject
	private MbcExtratoCirurgiaDAO mbcExtratoCirurgiaDAO;

	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO; 
	
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	
	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public void verficarPacienteSalaPreparo(Integer crgSeq, String nomeMicrocomputador, Date vinculoServidor) throws BaseException {

		List<MbcExtratoCirurgia> listaCirurgia = mbcExtratoCirurgiaDAO.obterExtratoAmbulatorioInternacaoPorSituacaoCrgSeq(crgSeq, DominioSituacaoCirurgia.CANC, DominioSituacaoCirurgia.PREP);
		
		if(listaCirurgia != null && !listaCirurgia.isEmpty()){
			MbcCirurgias cirurgia =  mbcCirurgiasDAO.obterPorChavePrimaria(crgSeq);
			DominioGrupoConvenio grupoConvenio = cirurgia.getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio();
			if (DominioGrupoConvenio.S.equals(grupoConvenio)){
				verificarAnamneseEEvolucao(cirurgia, nomeMicrocomputador, vinculoServidor);
			}
		}
	}

	private void verificarAnamneseEEvolucao(MbcCirurgias cirurgia, String nomeMicrocomputador, Date vinculoServidor) throws BaseException {
		
		List<MamAnamneses> listMamAnamneses = mamAnamnesesDAO.listarAnamenesePorCirurgia(cirurgia.getSeq());
		List<MamEvolucoes> listMamEvolucoes = mamEvolucoesDAO.listarEvolcoesPorCirurgia(cirurgia.getSeq());
		
		RapServidores servidorResponsavel = null;
		
		if (listMamAnamneses != null && !listMamAnamneses.isEmpty()) {
			servidorResponsavel = listMamAnamneses.get(0).getServidorValida();
			verificaProfissional(mamAnamnesesDAO.obterMamAnamnesesPorCirurgia(cirurgia.getSeq()), cirurgia, nomeMicrocomputador, vinculoServidor, servidorResponsavel);
			
		} else if (listMamEvolucoes != null && !listMamEvolucoes.isEmpty()) {
			servidorResponsavel = listMamEvolucoes.get(0).getServidor();
			verificaProfissional(mamEvolucoesDAO.obterMamEvolucoesPorCirurgia(cirurgia.getSeq()), cirurgia, nomeMicrocomputador, vinculoServidor, servidorResponsavel);
		}
	}
	
	private void verificaProfissional(List<DominioFuncaoProfissional> listaProfissionais, MbcCirurgias cirurgia, String nomeMicrocomputador, Date vinculoServidor, RapServidores servidorResponsavel) throws BaseException {

		if (listaProfissionais != null) {
			for (DominioFuncaoProfissional dominioFuncaoProfissional : listaProfissionais) {
				if (DominioFuncaoProfissional.MCO.equals(dominioFuncaoProfissional) || DominioFuncaoProfissional.MPF.equals(dominioFuncaoProfissional)) {
					inserirFatProcedAmbRealizado(cirurgia, nomeMicrocomputador, vinculoServidor, obterPhiMedicoOuPhiEnfermeiro(AghuParametrosEnum.P_CONS_MED_AMB_CANC), servidorResponsavel, true);
					break;
				} else if (DominioFuncaoProfissional.ENF.equals(dominioFuncaoProfissional)) {
					inserirFatProcedAmbRealizado(cirurgia, nomeMicrocomputador, vinculoServidor, obterPhiMedicoOuPhiEnfermeiro(AghuParametrosEnum.P_CONS_ENF_AMB_CANC), servidorResponsavel, false);
					break;
				}
			}
		}
	}
	
	private void inserirFatProcedAmbRealizado(MbcCirurgias cirurgia,
			String nomeMicrocomputador, Date vinculoServidor, FatProcedHospInternos fatProcedHospInternos, RapServidores servidorResponsavel, boolean isMedico) throws BaseException {
		
		faturamentoFacade.inserirFatProcedAmbRealizado(
				popularFatProcedAmbRealizado(cirurgia, servidorResponsavel, fatProcedHospInternos, isMedico), nomeMicrocomputador, vinculoServidor);
	}

	private FatProcedHospInternos obterPhiMedicoOuPhiEnfermeiro(AghuParametrosEnum parametro) throws ApplicationBusinessException{
		return faturamentoFacade.obterProcedimentoHospitalarInterno(parametroFacade.buscarAghParametro(parametro).getVlrNumerico().intValue());
	}

	private FatProcedAmbRealizado popularFatProcedAmbRealizado(MbcCirurgias cirurgia, RapServidores servidorResponsavel,
			FatProcedHospInternos fatProcedHospInternos, boolean isMedico) throws ApplicationBusinessException{

		FatConvenioSaudePlanoId id = new FatConvenioSaudePlanoId();
		id.setSeq(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO).getVlrNumerico().byteValue());
		id.setCnvCodigo(cirurgia.getConvenioSaudePlano().getId().getSeq().shortValue());

		return new FatProcedAmbRealizadoBuilder()
			.withDthrRealizado(cirurgia.getDataInicioCirurgia())
			.withSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO)
			.withLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B)
			.withQuantidade((short)1)
			.withUnidadeFuncional(cirurgia.getUnidadeFuncional())
			.withEspecialidade(cirurgia.getEspecialidade())
			.withPaciente(cirurgia.getPaciente())
			.withIndOrigem(isMedico ? DominioOrigemProcedimentoAmbulatorialRealizado.CIA : null)
			.withAtendimento(cirurgia.getAtendimento())
			.withConvenioSaudePlano(faturamentoFacade.obterFatConvenioSaudePlanoPorChavePrimaria(id))
			.withPpcCrgSeq(isMedico ? cirurgia.getSeq() : null)
			.withAlteradoPor(servidorLogadoFacade.obterServidorLogado().getUsuario())
			.withCriadoPor(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_USUARIO_CRIADO_POR).getVlrTexto())
			.withCriadoEm(new Date())
			.withServidor(cirurgia.getServidor())
			
			// bug competência já existia na revision 221245 fatProcedAmbRealizado.getFatCompetencia() 
			// linha 91 -> FatProcedAmbRealizado fatProcedAmbRealizado = new FatProcedAmbRealizado();
			.withFatCompetencia(null)
			
			.withServidorResponsavel(servidorResponsavel)
			.withProcedimentoHospitalarInterno(fatProcedHospInternos)
			.builder();
	}
}
