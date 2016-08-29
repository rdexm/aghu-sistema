package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoMapa;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.dao.AelConfigMapaDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.RelatorioMapaBioquimicaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaEpfVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaHematologiaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaHemoculturaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaUroculturaVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AelControleNumeroMapa;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;


@Stateless
public class AelConfigMapaON extends BaseBusiness {

	@EJB
	private AelConfigMapaRN aelConfigMapaRN;
	
	@EJB
	private AelControleNumeroMapaRN aelControleNumeroMapaRN;
	
	private static final Log LOG = LogFactory.getLog(AelConfigMapaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AelConfigMapaDAO aelConfigMapaDAO;

	private class RetornoBeforeReport{
		private Date pDataMapa;
		private Integer nrMapa;
		private String cpUnfDesc;
		
		public RetornoBeforeReport(Date pDataMapa, Integer nrMapa, String cpUnfDesc) {
			super();
			this.pDataMapa = pDataMapa;
			this.nrMapa = nrMapa;
			this.cpUnfDesc = cpUnfDesc;
		}
	}

	private static final long serialVersionUID = 3814273390311314936L;
	
	public enum AelConfigMapaONExceptionCode implements BusinessExceptionCode {
		ERRO_NENHUM_DADO_ENCONTRADO_EMISSAO_MAPA, P_SITUACAO_NA_AREA_EXECUTORA_NAO_ENCONTRADO;
	}
	
	
	public List<AelConfigMapa> pesquisarAelConfigMapaPorPrioridadeUnidadeFederativa( final AghUnidadesFuncionais unidadeFuncional, final String mapa, 
																					 final DominoOrigemMapaAmostraItemExame origem, final Integer firstResult, 
																					 final Integer maxResult, final String orderProperty, final boolean asc){
		
		final List<Short> seqsPriori = getAghuFacade().obterUnidadesFuncionaisHierarquicasPorCaract2(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS);
		List<Short> unfs = new ArrayList<Short>();
		if(!seqsPriori.isEmpty()){
			unfs = getAghuFacade().listarUnidadesFuncionaisPorUnfSeq(seqsPriori);
		}
		
		return getAelConfigMapaDAO().pesquisarAelConfigMapaPorPrioridadeUnidadeFederativa(unidadeFuncional, mapa, origem, unfs, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarAelConfigMapaPorPrioridadeUnidadeFederativaCount( final AghUnidadesFuncionais unidadeFuncional, final String mapa, final DominoOrigemMapaAmostraItemExame origem){
		final List<Short> seqsPriori = getAghuFacade().obterUnidadesFuncionaisHierarquicasPorCaract2(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS);
		List<Short> unfs = new ArrayList<Short>();
		if(!seqsPriori.isEmpty()){
			unfs = getAghuFacade().listarUnidadesFuncionaisPorUnfSeq(seqsPriori);
		}
		
		return getAelConfigMapaDAO().pesquisarAelConfigMapaPorPrioridadeUnidadeFederativaCount(unidadeFuncional, mapa, origem, unfs);
	}
	
	public List<RelatorioMapaBioquimicaVO> obterMapasBioquimicaVo( final AelConfigMapa aelConfigMapa, 
																   final DominioTipoImpressaoMapa tipoImpressao,
																   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador) throws BaseException{
		
		final RetornoBeforeReport resultReport = beforeReport( aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
		
		List<RelatorioMapaBioquimicaVO> vos = getAelConfigMapaDAO().obterMapasBioquimicaVo(resultReport.nrMapa, aelConfigMapa.getOrigem(), resultReport.pDataMapa, aelConfigMapa.getSeq(), aelConfigMapa.getAghUnidadesFuncionais().getSeq());
		
		for (RelatorioMapaBioquimicaVO vo : vos) {
		
			/* ,Decode(Aelc_Laudo_Orig_Pac(Soe.Seq),'A','Amb','I','L:'||  
	           aelc_local_pac(soe.atd_seq),'U','Urg','X','Ext',           
	           'D','Doa','H','Hod','C','Cir','N','RN',Null
	           ) 
	           ||'-'||
	           Aelc_Busca_Conv_Plan(Soe.Csp_Cnv_Codigo,Soe.Csp_Seq) Origem */
			
			
			final AelSolicitacaoExames solicExa = getSolicitacaoExameFacade().obterSolicitacaoExame(vo.getAmoSoeSeq());
			
			// Aelc_Laudo_Orig_Pac
			final DominioOrigemAtendimento laudoOrigem = getExamesLaudosFacade().buscaLaudoOrigemPaciente(vo.getSoeSeq());
			
			// aelc_local_pac
			final String localPac = solicExa.getAtendimento() != null ? 
												getSolicitacaoExameFacade().recuperarLocalPaciente(solicExa.getAtendimento())
												: "     ";
			
			// Aelc_Busca_Conv_Plan
			final String convenioPlano = getExamesFacade().buscarConvenioPlano(solicExa.getConvenioSaudePlano());
			
			vo.setOrigem(laudoOrigem + localPac + "-"+convenioPlano);
			
			// Aelc_Laudo_Prnt_Pac(Soe.Seq) Prontuario
			vo.setProntuario( getExamesFacade().buscarLaudoProntuarioPaciente(solicExa) ); 
			
			// Aelc_Laudo_Nome_Pac(Soe.Seq) Nome1
			vo.setPaciente( getExamesFacade().buscarLaudoNomePaciente(solicExa) ); 
			
			vo.setCpUnfDesc(resultReport.cpUnfDesc);
		}

		return vos;
	}
	
	public List<RelatorioMapaHemoculturaVO> obterMapasHemoculturaVO( final AelConfigMapa aelConfigMapa, 
			   final DominioTipoImpressaoMapa tipoImpressao,
			   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador) throws BaseException{
		
		final RetornoBeforeReport resultReport = beforeReport( aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
		
		List<RelatorioMapaHemoculturaVO> vos = getAelConfigMapaDAO().obterMapasHemoculturaVO(resultReport.nrMapa, aelConfigMapa.getOrigem(), resultReport.pDataMapa, aelConfigMapa.getSeq(), aelConfigMapa.getAghUnidadesFuncionais().getSeq());
		
		for (RelatorioMapaHemoculturaVO vo : vos) {
			
			final AelSolicitacaoExames solicExa = getSolicitacaoExameFacade().obterSolicitacaoExame(vo.getAmoSoeSeq());
			
			//Aelc_Laudo_Nome_Pac(Soe.Seq) Nome1
			vo.setPaciente( getExamesFacade().buscarLaudoNomePaciente(solicExa) ); 
			
			vo.setCpUnfDesc(resultReport.cpUnfDesc);
		}
		
		return vos;
	}

	public List<RelatorioMapaHematologiaVO> obterMapasHematologiaVO( final AelConfigMapa aelConfigMapa, 
			   final DominioTipoImpressaoMapa tipoImpressao,
			   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador) throws BaseException{
		
		final RetornoBeforeReport resultReport = beforeReport( aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
		
		List<RelatorioMapaHematologiaVO> vos = getAelConfigMapaDAO().obterMapasHematologiaVO(resultReport.nrMapa, aelConfigMapa.getOrigem(), resultReport.pDataMapa, aelConfigMapa.getSeq(), aelConfigMapa.getAghUnidadesFuncionais().getSeq());
		
		for (RelatorioMapaHematologiaVO vo : vos) {
			
			final AelSolicitacaoExames solicExa = getSolicitacaoExameFacade().obterSolicitacaoExame(vo.getAmoSoeSeq());
			
			// Aelc_Laudo_Orig_Pac
			final DominioOrigemAtendimento laudoOrigem = getExamesLaudosFacade().buscaLaudoOrigemPaciente(vo.getSoeSeq());
			
			// aelc_local_pac
			final String localPac = solicExa.getAtendimento() != null ? 
												getSolicitacaoExameFacade().recuperarLocalPaciente(solicExa.getAtendimento())
												: "     ";

			// Aelc_Busca_Conv_Plan
			final String convenioPlano = getExamesFacade().buscarConvenioPlano(solicExa.getConvenioSaudePlano());

			vo.setOrigem((laudoOrigem!=null?laudoOrigem.getDescricao().substring(0,3):"") + " - "+convenioPlano);
			
			vo.setQuarto(localPac);

			vo.setIdade((solicExa.getAtendimento()!=null)?solicExa.getAtendimento().getPaciente().getIdade().toString():(solicExa.getAtendimentoDiverso() != null ? solicExa.getAtendimentoDiverso().getAipPaciente().getIdade().toString() : null));
			
			//Aelc_Laudo_Nome_Pac(Soe.Seq) Nome1
			vo.setPaciente( getExamesFacade().buscarLaudoNomePaciente(solicExa)); 
			
			vo.setProntuario(getExamesFacade().buscarLaudoProntuarioPaciente(solicExa));
			
			vo.setCpUnfDesc(aelConfigMapa.getAghUnidadesFuncionais().getSigla());
			
			vo.setNroMapa(pNroMapa);
		}
		
		return vos;
	}

	public List<RelatorioMapaUroculturaVO> obterMapasUroculturaVO( final AelConfigMapa aelConfigMapa, 
			   final DominioTipoImpressaoMapa tipoImpressao,
			   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador) throws BaseException{
		
		final RetornoBeforeReport resultReport = beforeReport( aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
		
		List<RelatorioMapaUroculturaVO> vos = getAelConfigMapaDAO().obterMapasUroculturaVO(resultReport.nrMapa, aelConfigMapa.getOrigem(), resultReport.pDataMapa, aelConfigMapa.getSeq(), aelConfigMapa.getAghUnidadesFuncionais().getSeq());
		
		for (RelatorioMapaUroculturaVO vo : vos) {
			
			final AelSolicitacaoExames solicExa = getSolicitacaoExameFacade().obterSolicitacaoExame(vo.getAmoSoeSeq());
			
			//Aelc_Laudo_Nome_Pac(Soe.Seq) Nome1
			vo.setPaciente( getExamesFacade().buscarLaudoNomePaciente(solicExa) ); 
			
			AelSolicitacaoExames solicitacaoExame = getAelSolicitacaoExameDAO().obterPorChavePrimaria(vo.getIseSoeSeq());
			
			//aelc_laudo_sexo_pac
			vo.setSexo(getExamesFacade().obterLaudoSexoPaciente(solicitacaoExame));
			
			//aelc_laudo_idd_pac
			vo.setIdade(getAelConfigMapaRN().laudoIdadePaciente(vo.getIseSoeSeq()));
			
			vo.setCpUnfDesc(resultReport.cpUnfDesc);
		}
		
		return vos;
	}
	
	public List<RelatorioMapaEpfVO> obterMapasEpfVO( final AelConfigMapa aelConfigMapa, 
			   final DominioTipoImpressaoMapa tipoImpressao,
			   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador) throws BaseException{
		
		final RetornoBeforeReport resultReport = beforeReport( aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
		
		List<RelatorioMapaEpfVO> vos = getAelConfigMapaDAO().obterMapasEpfVO(resultReport.nrMapa, aelConfigMapa.getOrigem(), resultReport.pDataMapa, aelConfigMapa.getSeq(), aelConfigMapa.getAghUnidadesFuncionais().getSeq());
		
		for (RelatorioMapaEpfVO vo : vos) {
			
			final AelSolicitacaoExames solicExa = getSolicitacaoExameFacade().obterSolicitacaoExame(vo.getAmoSoeSeq());
			
			//Aelc_Laudo_Nome_Pac(Soe.Seq) Nome1
			vo.setPaciente( getExamesFacade().buscarLaudoNomePaciente(solicExa) ); 
			
			vo.setCpUnfDesc(resultReport.cpUnfDesc);
		}
		
		return vos;
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public RetornoBeforeReport beforeReport(final AelConfigMapa aelConfigMapa, final DominioTipoImpressaoMapa tipoImpressao, Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador) throws BaseException{
		final Date vSysdate = new Date();
		final IExamesFacade examesFacade = getExamesFacade();

		final StringBuffer cpUnfDesc = new StringBuffer();
		
		// Busca descrição da Unidade Funcional
		final AghUnidadesFuncionais cUnf = aelConfigMapa.getAghUnidadesFuncionais();
		
		cpUnfDesc.append(cUnf.getSeq()).append(" - ").append(cUnf.getDescricao());
		
		// Busca sit codigo na area executora 
		final AghParametros pSitAE = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		if(pSitAE == null){
			throw new ApplicationBusinessException(AelConfigMapaONExceptionCode.P_SITUACAO_NA_AREA_EXECUTORA_NAO_ENCONTRADO);
		}
		
		// Início do codigo para a Query de ATUALIZACAO NUMERO INTERNO
		if(DominioTipoImpressaoMapa.I.equals(tipoImpressao)){
			
			final List<AelControleNumeroMapa> controlesMapa = examesFacade.obterPorDataNumeroUnicoEOrigem(aelConfigMapa, vSysdate);
			
			final AelControleNumeroMapa controleMapa;
			if(controlesMapa == null || controlesMapa.isEmpty()){
				controleMapa = new AelControleNumeroMapa();
				controleMapa.setDtEmissaoMapa(vSysdate);
				controleMapa.setAghUnidadesFuncionais(cUnf);
				controleMapa.setAelConfigMapa(aelConfigMapa);
				controleMapa.setOrigem(aelConfigMapa.getOrigem());
				
			} else {
				 controleMapa = controlesMapa.get(0);
			}
			
			short vUltNroInterno = controleMapa.getUltNroInterno()   != null ? controleMapa.getUltNroInterno().shortValue() : 0;
			int vUltMapaImpresso = controleMapa.getUltMapaImpresso() != null ? controleMapa.getUltMapaImpresso().intValue() : 0;
			pNroMapa = vUltMapaImpresso + 1;
			pDataMapa = vSysdate;
			
			String vTexto = pSitAE.getVlrTexto();

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			// Obtem o guiche do usuário logado
			final AelCadGuiche guiche = getExamesPatologiaFacade().obterAelCadGuichePorUsuarioUnidadeExecutora( aelConfigMapa.getAghUnidadesFuncionais(), 
																											    servidorLogado.getUsuario(), DominioSituacao.A, null);
			// V_SOL_AMO      VARCHAR2(11):='0000000000';
			StringBuffer vSolAmo = new StringBuffer("0000000000");
			
			// V_ANT_SOL_AMO  VARCHAR2(11):='0000000000';
			StringBuffer vAntSolAmo = new StringBuffer("0000000000");
			
			final List<RelatorioMapaBioquimicaVO> vos = cAtuMapa( aelConfigMapa, vTexto, 
																  (guiche != null ? guiche.getSeq() : null));
			for (RelatorioMapaBioquimicaVO vo : vos) {
				vSolAmo = new StringBuffer();
				
				// ltrim(rtrim(TO_CHAR(v_mp.soe_seq,'0000000')))
				vSolAmo.append(StringUtil.adicionaZerosAEsquerda(vo.getAmoSoeSeq(), 7));
				
				// || ltrim(rtrim(TO_CHAR(v_mp.amo_seqp,'000')))
				vSolAmo.append(StringUtil.adicionaZerosAEsquerda(vo.getAmoSeqp(), 3));
				
				
				if(!vAntSolAmo.equals(vSolAmo)){
					vUltNroInterno++;
					final AelAmostras amostra = examesFacade.buscarAmostrasPorId(vo.getAmoSoeSeq(), vo.getAmoSeqp().shortValue());
					amostra.setNroInterno(vUltNroInterno);
					amostra.setConfigMapa(aelConfigMapa);
					
					examesFacade.persistirAelAmostra(amostra, false);
				}
				
				final List<AelAmostraItemExames> itensAmostra = examesFacade.buscarAelAmostraItemExamesPorAmostra(vo.getAmoSoeSeq(), vo.getAmoSeqp());
				
				for (AelAmostraItemExames itemAmostra : itensAmostra) {
					itemAmostra.setNroMapa(Integer.valueOf(vUltMapaImpresso + 1));
					itemAmostra.setDtImpMapa(vSysdate);
					itemAmostra.setOrigemMapa(aelConfigMapa.getOrigem());
					examesFacade.persistirAelAmostraItemExames(itemAmostra, false, nomeMicrocomputador);
				}
				
				vAntSolAmo = new StringBuffer(vSolAmo.toString());
			}
			
			if(!vos.isEmpty()){
				controleMapa.setUltMapaImpresso(Integer.valueOf(vUltMapaImpresso + 1));
				controleMapa.setUltNroInterno(Integer.valueOf(vUltNroInterno));
				
				getAelControleNumeroMapaRN().persistir(controleMapa);
				
			} 
			cpUnfDesc.append("    Nro do Mapa: ").append(pNroMapa).append("  ").append( guiche != null ? guiche.getDescricao() : "");
			
		} else {
			// ELSE  --:P_TIPO_IMPRESSAO = 'R'
			//	  --:cp_nro_mapa:=:P_NRO_MAPA; 	
			//	  null;
			//	 END IF;
			
			cpUnfDesc.append("    Nro do Mapa: ").append(pNroMapa).append("  ").append("Reimpressão de ").append(DateUtil.obterDataFormatada(pDataMapa, "dd/MM/yyyy"));
		}

		return new RetornoBeforeReport( pDataMapa, pNroMapa, cpUnfDesc.toString() );
	}
	
	private List<RelatorioMapaBioquimicaVO> cAtuMapa( final AelConfigMapa mapa, final String cSitCodigo, Short cguSeq ){
		
		final List<RelatorioMapaBioquimicaVO> preCAtuMapa = getAelConfigMapaDAO().cAtuMapa(mapa, cSitCodigo, cguSeq);
		
		IAghuFacade aghuFacade = getAghuFacade();
		final List<RelatorioMapaBioquimicaVO> cAtuMapa = new ArrayList<RelatorioMapaBioquimicaVO>();
		
		for (RelatorioMapaBioquimicaVO vo : preCAtuMapa) {
			DominioSimNao result = aghuFacade.verificarCaracteristicaDaUnidadeFuncional(vo.getUnfSeq(), ConstanteAghCaractUnidFuncionais.AUTOMACAO_ROTINA);
			switch (mapa.getOrigem()) {
				case I: // c_origem = 'I'
					// aghc_ver_caract_unf (soe.unf_seq, 'Automacao Rotina') = 'N'
					if( !result.isSim() || 
							// (aghc_ver_caract_unf (soe.unf_seq, 'Automacao Rotina') = 'S' AND ise.tipo_coleta = 'U')
							(result.isSim() && DominioTipoColeta.U.equals(vo.getTipoColeta()))
					  ){
						cAtuMapa.add(vo);
					}
				break;

				case A:	// c_origem = 'A'
					// aghc_ver_caract_unf(soe.unf_seq, 'Automacao Rotina') = 'S' AND ise.tipo_coleta = 'N'
					if(result.isSim() && DominioTipoColeta.N.equals(vo.getTipoColeta())){
						cAtuMapa.add(vo);
					}
				break;

				case T: // (c_origem = 'T')
					cAtuMapa.add(vo);				
				break;
			}
		}
		
		return cAtuMapa;
	}


	
	private AelConfigMapaDAO getAelConfigMapaDAO() {
		return aelConfigMapaDAO;
	}
	
	private AelControleNumeroMapaRN getAelControleNumeroMapaRN(){
		return aelControleNumeroMapaRN;
	}
	
	private IExamesPatologiaFacade getExamesPatologiaFacade(){
		return this.examesPatologiaFacade;
	}
	
	private IExamesFacade getExamesFacade(){
		return this.examesFacade;
	}
	
	private ISolicitacaoExameFacade getSolicitacaoExameFacade(){
		return this.solicitacaoExameFacade;
	}
	
	private IExamesLaudosFacade getExamesLaudosFacade(){
		return this.examesLaudosFacade;
	}

	private IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	private AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	private AelConfigMapaRN getAelConfigMapaRN() {
		return aelConfigMapaRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
