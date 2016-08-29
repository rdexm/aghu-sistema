package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoEspExecCirgDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasReservaHemoterapicaVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioCirurgiasReservaHemoterapicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioCirurgiasReservaHemoterapicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcSolicitacaoEspExecCirgDAO mbcSolicitacaoEspExecCirgDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	private static final long serialVersionUID = 2602336726448189350L;
	
	public enum RelatorioCirurgiasReservaHemoterapicaONExceptionCode implements	BusinessExceptionCode {
	MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
}

	
	public List<RelatorioCirurgiasReservaHemoterapicaVO> recuperarRelatorioCirurgiasReservaHemoterapicaVO(Short unfSeq, Date dataCirurgia, Boolean cirgComSolicitacao) throws ApplicationBusinessException  {
		
		List<MbcCirurgias> listaCirurgias = getMbcCirurgiasDAO().pesquisarCirurgiasPorUnidadeDataSolicitacao(unfSeq, null, dataCirurgia, cirgComSolicitacao, false, true, null);
		
		if(listaCirurgias.isEmpty()){
			throw new ApplicationBusinessException(RelatorioCirurgiasReservaHemoterapicaONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		//C1
		List<RelatorioCirurgiasReservaHemoterapicaVO> listaRelatorioCirurgiasReservaHemoterapicaVO = new ArrayList<RelatorioCirurgiasReservaHemoterapicaVO>();
		
		for(MbcCirurgias cirurgia : listaCirurgias){
			
			RelatorioCirurgiasReservaHemoterapicaVO vo = new RelatorioCirurgiasReservaHemoterapicaVO();
			
			vo.setReq(processarCfReqFormula(cirurgia.getSeq()));//RN1	
			vo.setHrInicioCirurgia(DateUtil.obterDataFormatada(cirurgia.getDataInicioCirurgia(), "HH:mm"));
			vo.setProntuario(CoreUtil.formataProntuario(cirurgia.getPaciente().getProntuario()));
			vo.setNome(cirurgia.getPaciente().getNome());
			vo.setDtNascimento(cirurgia.getPaciente().getDtNascimento());
			vo.setSala(cirurgia.getSalaCirurgica().getId().getSeqp());
			vo.setHrFimCirurgia(DateUtil.obterDataFormatada(cirurgia.getDataFimCirurgia(), "HH:mm"));			
			vo.setOrigem(DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia()) ? "INTERNAÇÃO" : (DominioOrigemPacienteCirurgia.A.equals(cirurgia.getOrigemPacienteCirurgia()) ? "AMBULATÓRIO" : null));
			vo.setQuarto(getEscalaCirurgiasON().pesquisaQuarto(cirurgia.getPaciente().getCodigo()));//MBCC_LOCAL_AIP_PAC
			vo.setConvenio(cirurgia.getConvenioSaudePlano().getConvenioSaude().getDescricao());
			vo.setCirurgiaoList(processarListaCirurgiao(cirurgia.getSeq()));//C4
			vo.setProcedimentosList(processarListaProcedimentos(cirurgia.getSeq()));//C2    
			vo.setComponentesList(processarListaComponentes(cirurgia.getSeq()));//C5   
			vo.setNecessidadesList(processarListaNecessidades(cirurgia.getSeq()));//C3   
			vo.setCrgSeq(cirurgia.getSeq());
						
			listaRelatorioCirurgiasReservaHemoterapicaVO.add(vo);
		}		
		
		return listaRelatorioCirurgiasReservaHemoterapicaVO;
	}
	
	//C2  
	private List<LinhaReportVO> processarListaProcedimentos(Integer crgSeq) {
		
		List<LinhaReportVO> listaProcedimentos= new ArrayList<LinhaReportVO>();
		
		List<MbcProcEspPorCirurgias> listProcEspPorCirurgias = getMbcProcEspPorCirurgiasDAO().listarMbcProcEspPorCirurgiasPorCrgSeq(crgSeq, true, false, false);
		for (MbcProcEspPorCirurgias procEspPorCirurgias : listProcEspPorCirurgias) {
			
			LinhaReportVO linhaReportProcedimento = new LinhaReportVO();			
			linhaReportProcedimento.setTexto1(procEspPorCirurgias.getProcedimentoCirurgico().getDescricao());
			
			listaProcedimentos.add(linhaReportProcedimento);			
		}	
		if(listaProcedimentos.isEmpty()){
			return null;
		}
		return listaProcedimentos;		
	}
	
	//C3
	private List<LinhaReportVO> processarListaNecessidades(Integer crgSeq) {
		List<LinhaReportVO> listaNecessidades = new ArrayList<LinhaReportVO>();
		
		List<MbcSolicitacaoEspExecCirg> listSolicitacaoEspExecCirg = getMbcSolicitacaoEspExecCirgsDAO().pesquisarMbcSolicitacaoEspExecCirgPorCrgSeqCaractUnf(crgSeq,
				ConstanteAghCaractUnidFuncionais.UNID_SOROLOGIA_DOADORES);
		for (MbcSolicitacaoEspExecCirg solicitacaoEspExecCirg : listSolicitacaoEspExecCirg) {
			
			LinhaReportVO linhaReportNecessidade = new LinhaReportVO();
			linhaReportNecessidade.setTexto1(solicitacaoEspExecCirg.getMbcNecessidadeCirurgica().getDescricao());
			
			listaNecessidades.add(linhaReportNecessidade);			
			
		}		
		if(listaNecessidades.isEmpty()){
			return null;
		}		
		return listaNecessidades;
	}
	
	//C4
	private List<LinhaReportVO> processarListaCirurgiao(Integer crgSeq) {
		
		List<LinhaReportVO> listaCirurgiao= new ArrayList<LinhaReportVO>();
		
		DominioFuncaoProfissional[] listFuncaoProfissional = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF};
		List<MbcProfCirurgias> listProfCirurgias = getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorCrgSeqFuncaoProfissional(crgSeq, listFuncaoProfissional,null);
		for (MbcProfCirurgias profCirurgias : listProfCirurgias) {
			
			LinhaReportVO linhaReportCirurgiao = new LinhaReportVO();
			if(profCirurgias.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsual() != null){
				linhaReportCirurgiao.setTexto1(profCirurgias.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsual());
			}else{
				linhaReportCirurgiao.setTexto1(profCirurgias.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
			}
			listaCirurgiao.add(linhaReportCirurgiao);			
		}
		if(listaCirurgiao.isEmpty()){
			return null;
		}
		return listaCirurgiao;
	}

	//C5
	private List<LinhaReportVO> processarListaComponentes(Integer crgSeq) {
		
		List<LinhaReportVO> listaComponentes= new ArrayList<LinhaReportVO>();
		
		pesquisarComponentesSanguineos(listaComponentes, crgSeq);
		
		pesquisarUnionProcedimentos(listaComponentes, crgSeq);
			
		if(listaComponentes.isEmpty()){
			return null;
		}
		return listaComponentes;		
	}

	private void pesquisarUnionProcedimentos(List<LinhaReportVO> listaComponentes, Integer crgSeq) {
		List<MbcProcEspPorCirurgias> listProcEspPorCirurgias = getMbcProcEspPorCirurgiasDAO().pesquisarProcEspCirurgicoPrincipalAgendamentoPorCrgSeq(crgSeq, DominioSituacao.A,Boolean.TRUE);
		for (MbcProcEspPorCirurgias procEspPorCirurgias : listProcEspPorCirurgias) {
		
			LinhaReportVO linhaReportComponente = new LinhaReportVO();
			if(procEspPorCirurgias.getProcedimentoCirurgico().getIndTipagemSangue()){
				linhaReportComponente.setTexto1("TIPAGEM SANGUÍNEA SOLICITADA");
				linhaReportComponente.setTexto2("");
				linhaReportComponente.setTexto3("");
			
				listaComponentes.add(linhaReportComponente);
			}
		}		
	}

	private void pesquisarComponentesSanguineos(List<LinhaReportVO> listaComponentes, Integer crgSeq) {
		List<MbcSolicHemoCirgAgendada> listSolicHemoCirgAgendada = getMbcSolicHemoCirgAgendadaDAO().pesquisarComponenteSanguineosEscalaCirurgica(crgSeq);
		for (MbcSolicHemoCirgAgendada solicHemoCirgAgendada : listSolicHemoCirgAgendada) {
		
			LinhaReportVO linhaReportComponente = new LinhaReportVO();	
			linhaReportComponente.setTexto1(solicHemoCirgAgendada.getAbsComponenteSanguineo().getDescricao());
			
			if(solicHemoCirgAgendada.getQtdeMl() != null){
				linhaReportComponente.setTexto2(solicHemoCirgAgendada.getQtdeMl().toString() + " ML");				
			}else{
				linhaReportComponente.setTexto2(solicHemoCirgAgendada.getQuantidade().toString() + " UN");		
			}
			
			StringBuilder tipo = new StringBuilder(50);			
			
			if(solicHemoCirgAgendada.getIndIrradiado()){
				tipo.append("IRRADIADO ");
			}
			if(solicHemoCirgAgendada.getIndFiltrado()){
				tipo.append("FILTRADO ");
			}
			if(solicHemoCirgAgendada.getIndLavado()){
				tipo.append("LAVADO ");
			}
			if(solicHemoCirgAgendada.getIndAutoTransfusao()){
				tipo.append("AUTO TRANSFUSAO");
			}
			
			linhaReportComponente.setTexto3(tipo.toString());				
		
			listaComponentes.add(linhaReportComponente);
		}		
	}	

	//RN1
	private String processarCfReqFormula(Integer crSseq) {
		
		List<MbcSolicHemoCirgAgendada> lista = getMbcSolicHemoCirgAgendadaDAO().pesquisarComponenteSanguineosEscalaCirurgica(crSseq);
		
		if(lista != null && !lista.isEmpty()){
			return "SIM";
		}
		return "NÃO";		
	}
		
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcSolicitacaoEspExecCirgDAO getMbcSolicitacaoEspExecCirgsDAO() {
		return mbcSolicitacaoEspExecCirgDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
		return mbcSolicHemoCirgAgendadaDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}
	
}
