package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.VisualizaCirurgiaCanceladaON.VisualizarCirurgiaCanceladaONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.vo.PacientesEmListaDeProcedimentosCanceladosVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasListaEsperaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioCirurgiasListaEsperaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioCirurgiasListaEsperaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	@EJB
	private VisualizaCirurgiaCanceladaON visualizaCirurgiaCanceladaON;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade iBlocoCirurgicoPortalPlanejamentoFacade;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6301887354402722984L;

	public List<RelatorioCirurgiasListaEsperaVO> obterListaEsperaPorUnidadeFuncionalEquipeEspecialidade(MbcProfAtuaUnidCirgs equipe, 
			Short espSeq, Short unfSeq, Integer pacCodigo) {
		
		List<RelatorioCirurgiasListaEsperaVO> lista = new ArrayList<RelatorioCirurgiasListaEsperaVO>(0);

		List<MbcAgendas> agendas = getMbcAgendasDAO().listarAgendaPorUnidadeEspecialidadeEquipePaciente(null, null, MbcAgendas.Fields.DTHR_INCLUSAO.toString(), true, 
				equipe.getId().getSerMatricula(), equipe.getId().getSerVinCodigo(), equipe.getId().getUnfSeq(), equipe.getId().getIndFuncaoProf(), 
				espSeq, unfSeq, pacCodigo);

		for (MbcAgendas agenda: agendas) {
			RelatorioCirurgiasListaEsperaVO vo = new RelatorioCirurgiasListaEsperaVO();
			
			vo.setDataInclusao(DateUtil.obterDataFormatada(agenda.getDthrInclusao(), "dd/MM/yyyy")); 
			vo.setProcedimento(CoreUtil.capitalizaTextoFormatoAghu(agenda.getProcedimentoCirurgico().getDescricao()));
			vo.setProntuario(CoreUtil.formataProntuario(agenda.getPaciente().getProntuario()));
			//AELC_TRATA_NOME
			vo.setNomePaciente(getBlocoCirurgicoPortalPlanejamentoFacade().obterNomeIntermediarioPacienteAbreviado(
					WordUtils.capitalizeFully(agenda.getPaciente().getNome())));
			if(agenda.getComentario() != null) {
				vo.setComentario(agenda.getComentario().replace("\n", " "));
			}
			
			lista.add(vo);
		}
			
		return lista;
	}
	
	public List<PacientesEmListaDeProcedimentosCanceladosVO> obterPacientesEmListaDProcedimentosCancelados(MbcProfAtuaUnidCirgs equipe, 
			Short espSeq, Short unfSeq, Integer pacCodigo) throws ApplicationBusinessException {
		
		//Recupera Parametro referente a motivo de cancelamento fixo para desmarcar cirurgia no Prontuario Online
		AghParametros parametroMotivoDesmarcar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);
		if(parametroMotivoDesmarcar == null){
			throw new ApplicationBusinessException(VisualizarCirurgiaCanceladaONExceptionCode.MENSAGEM_PARAMETRO_MOT_DESMARCAR_NAO_CONFIGURADO);
		}
		Short vlrNumerico = parametroMotivoDesmarcar.getVlrNumerico().shortValueExact();
		
		List<PacientesEmListaDeProcedimentosCanceladosVO> lista = new ArrayList<PacientesEmListaDeProcedimentosCanceladosVO>(0);

		List<MbcAgendas> agendas = getMbcAgendasDAO().listarAgendaPacientesEmListaProcedimentosCancelados(MbcAgendas.Fields.PACIENTE_NOME .toString(), true, 
				equipe.getId().getSerMatricula(), equipe.getId().getSerVinCodigo(), equipe.getId().getUnfSeq(), equipe.getId().getIndFuncaoProf(), 
				espSeq, unfSeq, pacCodigo);

		for (MbcAgendas agenda: agendas) {
			
			PacientesEmListaDeProcedimentosCanceladosVO vo = new PacientesEmListaDeProcedimentosCanceladosVO();
			
			vo.setDataCancelamento(DateUtil.obterDataFormatada(agenda.getDthrInclusao(), "dd/MM/yy")); 
			vo.setProcedimento(CoreUtil.capitalizaTextoFormatoAghu(agenda.getProcedimentoCirurgico().getDescricao()));
			vo.setProntuario(CoreUtil.formataProntuario(agenda.getPaciente().getProntuario()));
			//AELC_TRATA_NOME
			vo.setNomePaciente(getBlocoCirurgicoPortalPlanejamentoFacade().obterNomeIntermediarioPacienteAbreviado(
					WordUtils.capitalizeFully(agenda.getPaciente().getNome())));
			
			//obtém a data do cancelamento
			List<MbcCirurgias> listaDataCirurgiasCanceladas = getMbcCirurgiasDAO().pesquisarMotivoCirurgiasCanceladasOrdenaPorData(agenda.getSeq(), vlrNumerico);
			if (listaDataCirurgiasCanceladas != null && listaDataCirurgiasCanceladas.size() > 0) {
				vo.setDataCancelamento(DateUtil.obterDataFormatada(listaDataCirurgiasCanceladas.get(0).getData(), "dd/MM/yy").concat(" - "));

			}
			
			//obtém o número de cancelamentos
			vo.setComentario("nº canc: " + this.getMbcCirurgiasDAO().pesquisarMotivoCirurgiasCanceladasCount(agenda.getSeq(), vlrNumerico));
			
			//obtém o motivo
			String motivo = this.getVisualizaCirurgiaCanceladaON().obterMotivoCancelamento(agenda.getSeq(), vlrNumerico);
			if (motivo != null) {
				vo.setComentario(vo.getComentario().concat(" - " + motivo.substring(0, motivo.length()-1)));
			}
			
			if(agenda.getComentario() != null) {
				vo.setComentario(vo.getComentario().concat(" " + agenda.getComentario().replace("\n", " ")));
			}

			lista.add(vo);
		}
			
		return lista;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected VisualizaCirurgiaCanceladaON getVisualizaCirurgiaCanceladaON() {
		return visualizaCirurgiaCanceladaON;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
	
	protected IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		 return this.iBlocoCirurgicoPortalPlanejamentoFacade;
	}
}
