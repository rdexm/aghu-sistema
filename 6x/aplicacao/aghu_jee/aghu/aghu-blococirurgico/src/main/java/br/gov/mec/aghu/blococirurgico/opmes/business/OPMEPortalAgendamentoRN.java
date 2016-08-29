package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.math.BigDecimal;
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
import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMateriaisItemOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.GrupoExcludenteVO;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoHospitalarMaterialVO;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesListaGrupoExcludente;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.dominio.DominioSituacaoMaterialItem;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.dominio.DominioTabelaGrupoExcludente;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsavel pelas regras de negocio referentes a aba 
 * OPMEs da tela de Portal de Agendamento de Cirurgias / PDT 
 *
 */
@Stateless
public class OPMEPortalAgendamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(OPMEPortalAgendamentoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO;

	@Inject
	private MbcMateriaisItemOpmesDAO mbcMateriaisItemOpmesDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@EJB
	private OPMEPortalAgendamentoON oPMEPortalAgendamentoON;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
		
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4245278171014194456L;
	
	
	public enum OPMEPortalAgendamentoRNBusinessExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_REQ_FINALIZADA, MSG_ERRO_REQ_ANDAMENTO, MSG_ERRO_REQ_OBRIG , MSG_ERRO_REQ_SEM_ALTERACOES, MSG_ERRO_EXCL_MAT_PADRAO
	}

	// RN01_VERF_CONV
	public Boolean verificarConvenio(MbcAgendas agenda) {
		
		AghParametros convenioSusPadrao = this.parametroFacade.getAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		AghParametros susPlanoInternacao = this.parametroFacade.getAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
		Byte cspInt = susPlanoInternacao.getVlrNumerico().byteValue();
		Short cnvCod = convenioSusPadrao.getVlrNumerico().shortValue();
		
		if(cspInt.equals(agenda.getConvenioSaudePlano().getId().getSeq()) && cnvCod.equals(agenda.getConvenioSaudePlano().getId().getCnvCodigo())){
			return true;
		}
		//,CONVENIO_SAUDE_PLANO_CSP_SEQ("convenioSaudePlano.id.seq")
		//,CONVENIO_SAUDE_PLANO_CSP_CONV_SEQ("convenioSaudePlano.id.cnvCodigo")
		//InformacaoAgendaVO vo = new InformacaoAgendaVO(cadastroPlanejamentoVO, convenioSusPadrao, susPlanoInternacao);
		//return (vo.getCspCnvCodigo() == vo.getCnvSus() && vo.getCspSeq() == vo.getCspIntSeq());
		return false;
	}

	/*private InformacaoAgendaVO obterInformacaoAgendaVO(CadastroPlanejamentoVO cadastroPlanejamentoVO, AghParametros convenioSusPadrao, AghParametros susPlanoInternacao) {
		InformacaoAgendaVO vo = new InformacaoAgendaVO();
//		vo.setSeq(agenda.getSeq());
//		vo.setDtAgenda(agenda.getDtAgenda());
//		vo.setIndSituacao(agenda.getIndSituacao());
		if (cadastroPlanejamentoVO.getPaciente() != null) {
			vo.setPacCodigo(cadastroPlanejamentoVO.getPaciente().getCodigo());
		}
//		if (agenda.getProcedimentoCirurgico() != null) {
//			vo.setEprPciSeq(agenda.getProcedimentoCirurgico().getSeq());
//		}
//		if (agenda.getEspProcCirgs() != null) {
//			vo.setEprEspSeq(agenda.getEspProcCirgs().getId().getEspSeq());
//		}
		vo.setCspCnvCodigo(cadastroPlanejamentoVO.getFatConvenioSaude() == null ? convenioSusPadrao.getVlrNumerico().shortValue() : cadastroPlanejamentoVO.getFatConvenioSaude().getCodigo());
		vo.setCspSeq(cadastroPlanejamentoVO.getConvenioSaudePlano() == null ? susPlanoInternacao.getVlrNumerico().byteValue() : cadastroPlanejamentoVO.getConvenioSaudePlano().getId().getSeq());
		vo.setCnvSus(convenioSusPadrao.getVlrNumerico().shortValue());
		vo.setCspIntSeq(susPlanoInternacao.getVlrNumerico().byteValue());
//		if (agenda.getDthrInclusao() != null) {
//			Date dtBase = DateUtil.adicionaDias(agenda.getDthrInclusao(), cadastroPlanejamentoVO.getUnidadeFuncional().getQtdDiasLimiteCirg().intValue());
//			vo.setDtBase(dtBase);
//		}
		if (cadastroPlanejamentoVO.getUnidadeFuncional() != null) {
			vo.setQtdDiasLimiteCirg( cadastroPlanejamentoVO.getUnidadeFuncional().getQtdDiasLimiteCirg().intValue());
		}
		
		return vo;
	}*/
	
	
	public List<FatItensProcedHospitalar> consultarProcedimentoSUSVinculadoProcedimentoInterno(Integer pciSeq, Object param) {
		List<FatItensProcedHospitalar> results = getMbcRequisicaoOpmesDAO().consultarProcedimentoSUSVinculadoProcedimentoInterno(pciSeq, param);
		List<FatItensProcedHospitalar> unicos = new ArrayList<FatItensProcedHospitalar>();
		for (FatItensProcedHospitalar result : results) {
			if (!unicos.contains(result)) {
				result.getCodTabela();
				result.getDescricao();
				unicos.add(result);
			}
		}

		return unicos;
	}
	
	// RN 12 - 2
	public List<MbcRequisicaoOpmes> regrasGerais(MbcAgendas agenda) throws ApplicationBusinessException {

		// Executa 2.1. C04_REQ_OPM
		List<MbcRequisicaoOpmes> listaRequisicao = getMbcRequisicaoOpmesDAO().consultarListaRequisicoesPorAgenda(agenda.getSeq());
		List<MbcRequisicaoOpmes> listaAprovacao = new ArrayList<MbcRequisicaoOpmes>();
		
		for (MbcRequisicaoOpmes requisicao : listaRequisicao) {
			
			/*
			 * 2.2. Se a requisição já foi “concluída” ou “cancelada” (<REQUISICAO_OPMES>.<IND_SITUACAO> em 'CONCLUIDA', 'CANCELADA'):
			   2.2.1. Emite mensagem MSG_ERRO_REQ_FINALIZADA
			   2.2.2. Cancela fluxo
			 */
			if (DominioSituacaoRequisicao.CONCLUIDA.equals(requisicao.getSituacao()) || DominioSituacaoRequisicao.CANCELADA.equals(requisicao.getSituacao())) {
				throw new ApplicationBusinessException(OPMEPortalAgendamentoRNBusinessExceptionCode.MSG_ERRO_REQ_FINALIZADA);
			}
			
			/*
			 * 2.3. Se requisição já foi “autorizada” (<REQUISICAO_OPMES>.<IND_SITUACAO> em 'AUTORIZADA')
			   2.3.1. Abre modal para confirmar alteração com usuário. Emitir a mensagem MSG_MODAL_REQ_AUTORIZADA_CONF_OPER
			   2.3.2. Se confirmado, continua fluxo
			   2.3.3. Caso contrário, finaliza fluxo (sem alterações)
			 */
			if (DominioSituacaoRequisicao.AUTORIZADA.equals(requisicao.getSituacao())) {
				listaAprovacao.add(requisicao);
				continue;
			}
			
			/*
			 * 2.4. Se o processo está “em andamento” (<REQUISICAO_OPMES>.<IND_SITUACAO> diferente de ['INCOMPATIVEL', 'COMPATIVEL', 'AUTORIZADA', 'NAO_AUTORIZADA', 'CONCLUIDA', 'CANCELADA']):
			   TODO: 2.4.1. Emite mensagem MSG_ERRO_REQ_ANDAMENTO (parâmetro = C08_REQ_ETP.REQ_ETAPA)
	 		   2.4.2. Não permite alterações na requisição (dados na tela).
			   2.4.3. Cancela fluxo
			 */
			if (!DominioSituacaoRequisicao.INCOMPATIVEL.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.COMPATIVEL.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.AUTORIZADA.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.NAO_AUTORIZADA.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.CONCLUIDA.equals(requisicao.getSituacao())
				&& !DominioSituacaoRequisicao.CANCELADA.equals(requisicao.getSituacao())) {
				
				throw new ApplicationBusinessException(OPMEPortalAgendamentoRNBusinessExceptionCode.MSG_ERRO_REQ_ANDAMENTO);
			}
		}
		
		return listaAprovacao;
	}
	
	// RN 12 - 3/4
	public void alterarProcedimentoSus(MbcAgendas agenda) {
		List<MbcRequisicaoOpmes> listaRequisicao = getMbcRequisicaoOpmesDAO().consultarListaRequisicoesPorAgenda(agenda.getSeq());
		MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO = getMbcItensRequisicaoOpmesDAO();
		
		for (MbcRequisicaoOpmes requisicao : listaRequisicao) {
			requisicao.setSituacao(DominioSituacaoRequisicao.INCOMPATIVEL);
			requisicao.setObservacaoOpme(null);
			requisicao.setJustificativaRequisicaoOpme(null);
			List<MbcItensRequisicaoOpmes> itens = requisicao.getItensRequisicao();
			
			mbcItensRequisicaoOpmesDAO.deletarItensRequisicoesOpmePorRequisicaoSeq(requisicao.getSeq());
			
			if(itens != null && itens.size() > 0){
				for(MbcItensRequisicaoOpmes item : itens){
					this.blocoCirurgicoOpmesFacade.insereMbcItensRequisicaoOpmesJn(item, DominioOperacoesJournal.DEL);
				}	
			}
		}
	}

	public MbcRequisicaoOpmes obterRequisicaoOriginal(Short seq) {
		return getMbcRequisicaoOpmesDAO().obterRequisicaoOriginal(seq);
	}
	public boolean verificaAlteracoes(MbcRequisicaoOpmes requisicaoOpmes, MbcRequisicaoOpmes old) throws ApplicationBusinessException {
		
		if (old == null) {
			return true;
		}
		if (verificarAlteracoesRequisicao(requisicaoOpmes, old)) {
			return true;
		}
		if (old.getItensRequisicao().size() != requisicaoOpmes.getItensRequisicao().size()) {
			return true;
		}
		if (getAlteracaopItens(requisicaoOpmes, old)) {
			return true;
		}
		throw new ApplicationBusinessException(OPMEPortalAgendamentoRNBusinessExceptionCode.MSG_ERRO_REQ_SEM_ALTERACOES);
	}

	private boolean getAlteracaopItens(MbcRequisicaoOpmes requisicaoOpmes, MbcRequisicaoOpmes old) {
		for (MbcItensRequisicaoOpmes itemOld : old.getItensRequisicao()) {
			boolean excluido = true;
			//Verifica alteração dos itens
			for (MbcItensRequisicaoOpmes item : requisicaoOpmes.getItensRequisicao()) { 
				if (itemOld.getSeq().equals(item.getSeq())) {
					excluido = false;
					if (getAlteracaoItem(itemOld, item)) {
						return true;
					}
					for (MbcMateriaisItemOpmes materialOld : itemOld.getMateriaisItemOpmes()) {
						//verifica alteração dos materiais
						for (MbcMateriaisItemOpmes material : item.getMateriaisItemOpmes()) {
							if (materialOld.getSeq().equals(material.getSeq())) {
								if (getAlteracaoMaterial(materialOld,material)) {
									return true;
								}
							}
						}
					}
				}
				
			}
			if (excluido) {
				return true;
			}
		}
		return false;
	}

	private boolean getAlteracaoMaterial(MbcMateriaisItemOpmes materialOld,
			MbcMateriaisItemOpmes material) {
		if (CoreUtil.modificados(materialOld.getMaterial(), material.getMaterial()) ||
				CoreUtil.modificados(materialOld.getProcedHospInternos(), material.getProcedHospInternos()) ||
				CoreUtil.modificados(materialOld.getQuantidadeConsumida(), material.getQuantidadeConsumida()) ||
				CoreUtil.modificados(materialOld.getQuantidadeSolicitada(), material.getQuantidadeSolicitada()) ||
				CoreUtil.modificados(materialOld.getSituacao(), material.getSituacao())) {
			return true;
		}
		return false;
	}

	private boolean getAlteracaoItem(MbcItensRequisicaoOpmes itemOld,
			MbcItensRequisicaoOpmes item) {
		if (CoreUtil.modificados(itemOld.getEspecificacaoNovoMaterial(), item.getEspecificacaoNovoMaterial()) ||
				getAlteracaoProcedimento(itemOld, item) ||
				CoreUtil.modificados(itemOld.getIndAutorizado(), item.getIndAutorizado()) ||
				CoreUtil.modificados(itemOld.getIndCompativel(), item.getIndCompativel()) ||
				CoreUtil.modificados(itemOld.getIndConsumido(), item.getIndConsumido()) ||
				CoreUtil.modificados(itemOld.getMateriaisItemOpmes(), item.getMateriaisItemOpmes()) ||
				CoreUtil.modificados(itemOld.getQuantidadeAutorizadaHospital(), item.getQuantidadeAutorizadaHospital()) ||
				CoreUtil.modificados(itemOld.getQuantidadeAutorizadaSus(), item.getQuantidadeAutorizadaSus()) || 
				CoreUtil.modificados(itemOld.getQuantidadeConsumida(), item.getQuantidadeConsumida()) ||
				CoreUtil.modificados(itemOld.getQuantidadeSolicitada(), item.getQuantidadeSolicitada()) ||
				CoreUtil.modificados(itemOld.getRequerido(), item.getRequerido()) ||
				CoreUtil.modificados(itemOld.getRequisicaoOpmes(), item.getRequisicaoOpmes()) ||
				CoreUtil.modificados(itemOld.getSolicitacaoNovoMaterial(), item.getSolicitacaoNovoMaterial()) ||
				CoreUtil.modificados(itemOld.getValorUnitarioIph(), item.getValorUnitarioIph())) {
			return true;
		}
		return false;
	}

	private Boolean getAlteracaoProcedimento(MbcItensRequisicaoOpmes itemOld, MbcItensRequisicaoOpmes item) {
		if ((item.getFatItensProcedHospitalar() == null && itemOld.getFatItensProcedHospitalar() != null) || 
				(item.getFatItensProcedHospitalar() != null && itemOld.getFatItensProcedHospitalar() == null)) {
			return true;
		}
		if (item.getFatItensProcedHospitalar() != null && itemOld.getFatItensProcedHospitalar() != null) {
			if (!itemOld.getFatItensProcedHospitalar().getId().equals(item.getFatItensProcedHospitalar().getId())) {
				return true;
			}
		}
		
		return false;
	}

	private boolean verificarAlteracoesRequisicao(
			MbcRequisicaoOpmes requisicaoOpmes, MbcRequisicaoOpmes old) {
		if (CoreUtil.modificados(old.getIndAutorizado(), requisicaoOpmes.getIndAutorizado()) ||
				CoreUtil.modificados(old.getIndCompativel(), requisicaoOpmes.getIndCompativel()) ||
				CoreUtil.modificados(old.getIndConsAprovacao(), requisicaoOpmes.getIndConsAprovacao()) ||
				CoreUtil.modificados(old.getJustificativaConsumoOpme(), requisicaoOpmes.getJustificativaConsumoOpme()) ||
				CoreUtil.modificados(old.getJustificativaRequisicaoOpme(), requisicaoOpmes.getJustificativaRequisicaoOpme()) ||
				CoreUtil.modificados(old.getObservacaoOpme(), requisicaoOpmes.getObservacaoOpme()) ||
				CoreUtil.modificados(old.getSituacao(), requisicaoOpmes.getSituacao())) {
			return true;
		}
		return false;
	}
	// RN 04 
//	public void consultarSituacaoProcessoAutorizacao(MbcAgendas agenda) {
//		List<MbcRequisicaoOpmes> listaRequisicao = getMbcRequisicaoOpmesDAO().consultarListaRequisicoesPorAgenda(agenda.getSeq());
//		
//		if (listaRequisicao == null || listaRequisicao.isEmpty()) {
//			// RN 05
//			return;
//		} else {
//			for (MbcRequisicaoOpmes requisicao : listaRequisicao) {
//				AghWFTemplateEtapa etapa = getAghWFTemplateEtapaDAO().consultarEtapaWorkflowRequisicao(requisicao.getSeq());
//				requisicao.setSituacaoProcessoAutorizacao(etapa);
//			}
//			// RN 05
//		}
//	}
	
	public void persistRequisicao(MbcRequisicaoOpmes requisicao, boolean persist) {
		if (persist) {
			this.mbcRequisicaoOpmesDAO.persistir(requisicao);
			flush();
			this.blocoCirurgicoOpmesFacade.insereMbcRequisicaoOpmes(requisicao, DominioOperacoesJournal.INS);
		}
	}
	
	public Integer verificarPrazoAgenda(MbcAgendas agenda, MbcRequisicaoOpmes requisicaoOpmes) throws ApplicationBusinessException {
		boolean possuiItens = verificaItem(requisicaoOpmes);
		if (!possuiItens) {
			return getOpmePortalAgendamentoON().verificarPrazoAgenda(agenda);
		}
		return 0;
	}
	
	public void concluirRequisicaoOpmes(MbcAgendas agenda, AipPacientes paciente, AghUnidadesFuncionais unidadeFuncional, MbcRequisicaoOpmes requisicao, boolean persist) throws ApplicationBusinessException {
		persistRequisicao(requisicao, persist);
	}
	
	private boolean verificaItem(MbcRequisicaoOpmes requisicao) {
		List<MbcItensRequisicaoOpmes> itensSolicitados = new ArrayList<MbcItensRequisicaoOpmes>();
		for (MbcItensRequisicaoOpmes item : requisicao.getItensRequisicao()) {
			if (item.getQuantidadeSolicitada() > 0) {
				itensSolicitados.add(item);
			}
		}
		
		if (itensSolicitados.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public void persistirRequisicaoOpme(MbcRequisicaoOpmes requisicao, List<MbcItensRequisicaoOpmes> itensExcluidos, List<MbcOpmesVO> listaClone, Boolean zeraFluxo) {
		RapServidores rapServidores = null;
		if(requisicao.getAgendas().getServidorAlteradoPor() != null) {
			rapServidores = requisicao.getAgendas().getServidorAlteradoPor();
		} else {
			rapServidores = requisicao.getAgendas().getServidor();
		}
		Boolean inserir = Boolean.TRUE;
		
		if (requisicao.getSeq() == null) {
			requisicao.setCriadoEm(new Date());
			requisicao.setRapServidores(rapServidores);
		} else {
			inserir = Boolean.FALSE;
		}
		requisicao.setModificadoEm(new Date());
		requisicao.setRapServidoresModificacao(rapServidores);
		
		
		if(requisicao.getFluxo() == null && DominioSituacaoRequisicao.CANCELADA.equals(requisicao.getSituacao())){
			requisicao.setSituacao(DominioSituacaoRequisicao.COMPATIVEL);
			for (MbcItensRequisicaoOpmes item : requisicao.getItensRequisicao()) {
				if(item.getIndCompativel() != null){
					if(item.getIndCompativel() == false){
						requisicao.setSituacao(DominioSituacaoRequisicao.INCOMPATIVEL);
					}
				}
			}
		}
		//requisicao editada tera novo fluxo e removida a data dim, sera editada após cancelamento ou não autorizacao
		if(zeraFluxo){
			requisicao.setFluxo(null);
			requisicao.setDataFim(null);
		}
		if(requisicao.getSeq() != null){
			getMbcRequisicaoOpmesDAO().atualizar(requisicao);
		}else{
			getMbcRequisicaoOpmesDAO().persistir(requisicao);
		}
		
		getMbcRequisicaoOpmesDAO().flush();
		
		if(inserir){
			this.blocoCirurgicoOpmesFacade.insereMbcRequisicaoOpmes(requisicao, DominioOperacoesJournal.INS);
		} else {
			this.blocoCirurgicoOpmesFacade.insereMbcRequisicaoOpmes(requisicao, DominioOperacoesJournal.UPD);
		}
		
		persistItens(requisicao, rapServidores, listaClone);
		excluirItens(itensExcluidos);

		
	}

	private void excluirItens(List<MbcItensRequisicaoOpmes> itensExcluidos) {
		if (itensExcluidos != null) {
			for (MbcItensRequisicaoOpmes item : itensExcluidos) {
				excluirMateriais(item.getMateriaisItemOpmes());
				item.getMateriaisItemOpmes().removeAll(item.getMateriaisItemOpmes());
				if(item.getSeq() != null){
					MbcItensRequisicaoOpmes entity = getMbcItensRequisicaoOpmesDAO().obterPorChavePrimaria(item.getSeq());
					this.blocoCirurgicoOpmesFacade.insereMbcItensRequisicaoOpmesJn(entity, DominioOperacoesJournal.DEL);
					getMbcItensRequisicaoOpmesDAO().remover(entity);
				}
			}
		}
	}

	private void persistItens(MbcRequisicaoOpmes requisicao, RapServidores rapServidores, List<MbcOpmesVO> listaClone) {
		for (MbcItensRequisicaoOpmes item : requisicao.getItensRequisicao()) {
			
			Boolean journalPersistir = Boolean.FALSE;
			
			if (item.getSeq() == null) {
				item.setCriadoEm(new Date());
				item.setRapServidores(rapServidores);
				journalPersistir = Boolean.TRUE;
			}
			item.setModificadoEm(new Date());
			item.setRapServidoresModificacao(rapServidores);
			item.setRequisicaoOpmes(requisicao);
			
			setQuantidadeSolicitada(item);
			if(DominioRequeridoItemRequisicao.NRQ.equals(item.getRequerido()) || DominioRequeridoItemRequisicao.REQ.equals(item.getRequerido())){
				if(item.getQuantidadeSolicitada() > 0){
					item.setRequerido(DominioRequeridoItemRequisicao.REQ);
				}
			}
			
			if(item.getSeq() == null){
				this.mbcItensRequisicaoOpmesDAO.persistir(item);
			}else{
				this.mbcItensRequisicaoOpmesDAO.merge(item);
			}
			
			this.mbcItensRequisicaoOpmesDAO.flush();
			
			if(journalPersistir){
				this.blocoCirurgicoOpmesFacade.insereMbcItensRequisicaoOpmesJn(item, DominioOperacoesJournal.INS);
			} else {
				
				for(MbcOpmesVO vo : listaClone){
					
					Boolean encontrou = Boolean.FALSE;
					
					if(vo.getItemSeq().equals(item.getSeq())){
						
						//if(item.getMateriaisItemOpmes() != null){
							//for(MbcMateriaisItemOpmes entidade : item.getMateriaisItemOpmes()){
								//if(vo.getCodigoMaterial().equals(entidade.getMaterial().getCodigo())){
									if(!vo.getQtdeSol().equals(item.getQuantidadeSolicitada())){
										this.blocoCirurgicoOpmesFacade.insereMbcItensRequisicaoOpmesJn(item, DominioOperacoesJournal.UPD);
										encontrou = Boolean.TRUE;
										break;
									}
								//}	
							//}
						//}
					}
					if(encontrou){
						break;
					} else if(item.getFatItensProcedHospitalar() != null && vo.getCodTabela() != null && vo.getCodTabela().equals(item.getFatItensProcedHospitalar().getCodTabela())){
						if(!vo.getQtdeSol().equals(item.getQuantidadeSolicitada())){
							this.blocoCirurgicoOpmesFacade.insereMbcItensRequisicaoOpmesJn(item, DominioOperacoesJournal.UPD);
							break;
						}	
					}
				}	
			}
			persistMateriais(item, listaClone, rapServidores);
		}
	}

	private void setQuantidadeSolicitada(MbcItensRequisicaoOpmes item) {
		if (item.getMateriaisItemOpmes() != null && !item.getMateriaisItemOpmes().isEmpty()) {
			Integer qtdeSolicitada = 0;
			for (MbcMateriaisItemOpmes materiaisItemOpmes : item.getMateriaisItemOpmes()) {
				qtdeSolicitada = (qtdeSolicitada + (materiaisItemOpmes.getQuantidadeSolicitada() == null ? 0 : materiaisItemOpmes.getQuantidadeSolicitada()));
			}
			item.setQuantidadeSolicitada(qtdeSolicitada);
		}
	}
	

	private void excluirMateriais(List<MbcMateriaisItemOpmes> materiaisItemOpmes) {
		for (MbcMateriaisItemOpmes itemMaterial : materiaisItemOpmes) {
			if(itemMaterial.getSeq() != null){
				MbcMateriaisItemOpmes item = getMbcMateriaisItemOpmesDAO().obterPorChavePrimaria(itemMaterial.getSeq());
				this.blocoCirurgicoOpmesFacade.insereMbcMateriaisItemOpmesJn(item, DominioOperacoesJournal.DEL);
				getMbcMateriaisItemOpmesDAO().remover(item);
			}
		}
	}

	private void persistMateriais(MbcItensRequisicaoOpmes item, List<MbcOpmesVO> listaClone, RapServidores rapServidores) {
		for (MbcMateriaisItemOpmes itemMaterial : item.getMateriaisItemOpmes()) {
			//Boolean journalPersistir = Boolean.FALSE;
			if (itemMaterial.getSeq() == null) {
				itemMaterial.setCriadoEm(new Date());
				itemMaterial.setRapServidores(rapServidores);
				//journalPersistir = Boolean.TRUE;
			}
			//carregando entidade para a sessao
			if (itemMaterial.getScoMarcaComercial() != null) {
				ScoMarcaComercial marca = this.comprasFacade.obterMarcaComercialPorCodigo(itemMaterial.getScoMarcaComercial().getCodigo());
				itemMaterial.setScoMarcaComercial(marca);
			}
			
			itemMaterial.setModificadoEm(new Date());
			itemMaterial.setRapServidoresModificacao(rapServidores);
			itemMaterial.setItensRequisicaoOpmes(item);
			
			if (itemMaterial.getSeq() == null) {
				getMbcMateriaisItemOpmesDAO().persistir(itemMaterial);
			}else{
				getMbcMateriaisItemOpmesDAO().merge(itemMaterial);
			}
			
			//getMbcMateriaisItemOpmesDAO().flush();
			
//			if(journalPersistir){
//				this.blocoCirurgicoOpmesFacade.insereMbcMateriaisItemOpmesJn(itemMaterial, DominioOperacoesJournal.INS);
//			} else {
//				for(MbcOpmesVO vo : listaClone){
//					if(itemMaterial.getMaterial().getCodigo().equals(vo.getCodigoMaterial())){
//						if(!itemMaterial.getQuantidadeSolicitada().equals(vo.getQtdeSol())){
//							this.blocoCirurgicoOpmesFacade.insereMbcMateriaisItemOpmesJn(itemMaterial, DominioOperacoesJournal.UPD);
//						}	
//					}
//				}	
//			}
		}
	}
	
	private OPMEPortalAgendamentoON getOpmePortalAgendamentoON() {
		return oPMEPortalAgendamentoON;
	}
	
	private MbcRequisicaoOpmesDAO getMbcRequisicaoOpmesDAO(){
		return mbcRequisicaoOpmesDAO;
	}

	public MbcItensRequisicaoOpmes obterItensRequisicao(List<MbcItensRequisicaoOpmes> itensRequisicoesOpmes, ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO, MbcRequisicaoOpmes requisicaoOpmes) {
		MbcItensRequisicaoOpmes itensRequisicaoOpmes = new MbcItensRequisicaoOpmes(itemProcedimentoHospitalarMaterialVO.getCmp(), requisicaoOpmes);
		for (MbcItensRequisicaoOpmes mbcItensRequisicaoOpmes : itensRequisicoesOpmes) {
			if (mbcItensRequisicaoOpmes.getFatItensProcedHospitalar().equals(itemProcedimentoHospitalarMaterialVO.getCmp())) {
				return mbcItensRequisicaoOpmes;
			}
		}
		return itensRequisicaoOpmes;
	}
	
	public MbcOpmesVO adicionar(MbcItensRequisicaoOpmes item) {
		MbcMateriaisItemOpmes materiaisItemOpmes = null;
		if (item.getMateriaisItemOpmes() != null && !item.getMateriaisItemOpmes().isEmpty()) {
			materiaisItemOpmes = item.getMateriaisItemOpmes().iterator().next();
		}
		
		return adicionar(item, true, materiaisItemOpmes);
	}
	
	public MbcOpmesVO adicionar(MbcItensRequisicaoOpmes item, boolean excluir, MbcMateriaisItemOpmes materiaisItemOpmes) {
		Long iphCompCod = null;
		String iphCompDescr = null;
		if (item.getFatItensProcedHospitalar() != null) {
			iphCompCod = item.getFatItensProcedHospitalar().getCodTabela();
			iphCompDescr = item.getFatItensProcedHospitalar().getDescricao();
		}
		MbcOpmesVO vo = new MbcOpmesVO(item, excluir, iphCompCod, iphCompDescr, materiaisItemOpmes);
		
		new OPMEPortalAgendamentoON().calculaQuantidadeAutorizada(vo);
		vo.setCodTabela(iphCompCod);
		return vo;
	}
	
	public MbcItensRequisicaoOpmes buscaCriaRequisicao(List<MbcItensRequisicaoOpmes> itensRequisicoesOpmes, ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO, MbcRequisicaoOpmes requisicaoOpmes) {
		MbcItensRequisicaoOpmes itensRequisicaoOpmes = criaItemRequisicao(itemProcedimentoHospitalarMaterialVO, requisicaoOpmes);
		for (MbcItensRequisicaoOpmes mbcItensRequisicaoOpmes : itensRequisicoesOpmes) {
			if (mbcItensRequisicaoOpmes.getFatItensProcedHospitalar().equals(itemProcedimentoHospitalarMaterialVO.getCmp())) {
				return mbcItensRequisicaoOpmes;
			}
		}
		return itensRequisicaoOpmes;
	}
	
	public MbcOpmesListaGrupoExcludente consultaItensProcedimento(Integer pciSeq, FatItensProcedHospitalar procedimentoSus, MbcRequisicaoOpmes requisicaoOpmes) {
		
		List<ItemProcedimentoHospitalarMaterialVO> itens = getMbcRequisicaoOpmesDAO().consultarItensProcedimentoHospitalarMateriais(pciSeq, procedimentoSus.getSeq(), procedimentoSus.getId().getPhoSeq());
		
		List<MbcOpmesVO> opmes = new ArrayList<MbcOpmesVO>();
		List<MbcItensRequisicaoOpmes> itensRequisicoesOpmes = new ArrayList<MbcItensRequisicaoOpmes>();
		
		// Lista que armazena os grupos de excludentes
		List<GrupoExcludenteVO> listaGrupoExcludentes = new ArrayList<GrupoExcludenteVO>();
		Integer codigoGrupo = 1;
		
		if (requisicaoOpmes != null && requisicaoOpmes.getItensRequisicao() != null) {
			requisicaoOpmes.getItensRequisicao().removeAll(requisicaoOpmes.getItensRequisicao());
			
			
			for (ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO : itens) {
				itemProcedimentoHospitalarMaterialVO.setCmp(getMbcRequisicaoOpmesDAO().getFatItensProcedHospitalar(itemProcedimentoHospitalarMaterialVO.getIphCompSeq(), itemProcedimentoHospitalarMaterialVO.getIphCompPho()));
				
				MbcItensRequisicaoOpmes itensRequisicaoOpmes = buscaCriaRequisicao(itensRequisicoesOpmes, itemProcedimentoHospitalarMaterialVO, requisicaoOpmes);
				
				codigoGrupo = getCorExibicaoItem(itensRequisicaoOpmes, listaGrupoExcludentes, codigoGrupo); 
				
				Short qtMaxima = itemProcedimentoHospitalarMaterialVO.getQtdMaxima();
				if (qtMaxima == null) {
					qtMaxima = itemProcedimentoHospitalarMaterialVO.getMaxQtdConta();
				}
				itensRequisicaoOpmes.setQuantidadeAutorizadaSus((short)(itensRequisicaoOpmes.getQuantidadeAutorizadaSus() + qtMaxima));
				BigDecimal vlrUnitario = getSomaValoresUnitarios(itemProcedimentoHospitalarMaterialVO);
				itensRequisicaoOpmes.setValorUnitarioIph(itensRequisicaoOpmes.getValorUnitarioIph().add(vlrUnitario));
				MbcMateriaisItemOpmes materiaisItemOpmes = null;
				if (itemProcedimentoHospitalarMaterialVO.getMaterial() != null) {
					materiaisItemOpmes = new MbcMateriaisItemOpmes();
					materiaisItemOpmes.setSituacao(DominioSituacaoMaterialItem.A);
					materiaisItemOpmes.setItensRequisicaoOpmes(itensRequisicaoOpmes);
					if (itemProcedimentoHospitalarMaterialVO.getCodigoMat() != null) {
						materiaisItemOpmes.setProcedHospInternos(itemProcedimentoHospitalarMaterialVO.getCmpPhi());
						materiaisItemOpmes.setMaterial(itemProcedimentoHospitalarMaterialVO.getMaterial());
					}
	
					materiaisItemOpmes.setQuantidadeSolicitada(itemProcedimentoHospitalarMaterialVO.getMioQtdSolic() == null ? 0 : itemProcedimentoHospitalarMaterialVO.getMioQtdSolic());
					itensRequisicaoOpmes.getMateriaisItemOpmes().add(materiaisItemOpmes);
					
				}
				MbcOpmesVO vo = criar(itensRequisicaoOpmes, false, itemProcedimentoHospitalarMaterialVO.getIphCompCod(), itemProcedimentoHospitalarMaterialVO.getIphCompDscr(), materiaisItemOpmes);
				opmes.add(vo);
			}
			
			montaQuebra(opmes, requisicaoOpmes,listaGrupoExcludentes);
			
			atualizarRequisicaoCompatibilidade(requisicaoOpmes);
		}
		for (MbcOpmesVO mbcOpmesVO : opmes) {
			oPMEPortalAgendamentoON.calculaQuantidadeAutorizada(mbcOpmesVO);
		}
		MbcOpmesListaGrupoExcludente voTransporte = new MbcOpmesListaGrupoExcludente();
		voTransporte.setListaPesquisada(opmes);
		voTransporte.setListaGrupoExcludente(listaGrupoExcludentes);
		return voTransporte;
	}
	

	private Integer getCorExibicaoItem(MbcItensRequisicaoOpmes itemRequisicao, List<GrupoExcludenteVO> listaGrupoExcludentes, Integer codigoGrupo) {
		// #33880 - 4) Identificação dos Grupos de Excludências (Identificação visual):
		List<ItemProcedimentoVO> grupoExcludencia = getMbcRequisicaoOpmesDAO().consultarExcludenciaMaterial(itemRequisicao.getFatItensProcedHospitalar().getId().getPhoSeq(), itemRequisicao.getFatItensProcedHospitalar().getId().getSeq());
		List<GrupoExcludenteVO> listaGruposAux = new ArrayList<GrupoExcludenteVO>();
		listaGruposAux.addAll(listaGrupoExcludentes);
		/**
		 * a) Se o material recuperado no passo 3 NÃO possui materiais “mutualmente exclusivos”, então o registro não deve ter tratamento de cores (não pertence a nenhum grupo).
		 */
		
		if (grupoExcludencia != null && !grupoExcludencia.isEmpty()) {
			/**
			 * b) Caso contrário (possui materiais “mutualmente exclusivos”): Verifica se ao menos um “material excludente” está no [RecordSet Grupos Excludentes]:
			 */
			if (listaGrupoExcludentes.isEmpty()) {
				codigoGrupo = adicionarGrupo(listaGrupoExcludentes, grupoExcludencia, codigoGrupo, itemRequisicao);
			} else {
				boolean achou = false;
				for (GrupoExcludenteVO gex : listaGruposAux) {
					for (ItemProcedimentoVO vo : grupoExcludencia) {
						achou = getExistenciaItemGrupoExcl(vo, gex);
						if (achou) {
							gex.getListaGrupoExcludente().addAll(grupoExcludencia);
							break;
						}
					}
					if (achou) {
						break;
					}
				}
				if (!achou) {
					codigoGrupo = adicionarGrupo(listaGrupoExcludentes, grupoExcludencia, codigoGrupo, itemRequisicao);
				}
			}
		}
		
		return codigoGrupo;
	}

	private boolean getExistenciaItemGrupoExcl(ItemProcedimentoVO item, GrupoExcludenteVO gex) {
		// Flag para verificar se o item já está dentro de um grupo que possui cor.
		boolean achou = false;
		for (ItemProcedimentoVO itemGrupoVo : gex.getListaGrupoExcludente()) {
			if (item.getIpxPhoSeq().equals(itemGrupoVo.getIpxPhoSeq()) 
				&& item.getIpxSeq().equals(itemGrupoVo.getIpxSeq())) {
				achou = true;
			}
		}
		return achou;
	}

	private Integer adicionarGrupo(List<GrupoExcludenteVO> listaGrupoExcludentes, List<ItemProcedimentoVO> grupoExcludencia, Integer codigoGrupo, MbcItensRequisicaoOpmes itemRequisicao) {
		GrupoExcludenteVO ge = new GrupoExcludenteVO();
		ge.setCodigo(codigoGrupo);
		ge.setCor(DominioTabelaGrupoExcludente.getInstance(codigoGrupo));
		ge.setListaGrupoExcludente(grupoExcludencia);
		ge.setIphPhoSeq(itemRequisicao.getFatItensProcedHospitalar().getId().getPhoSeq());
		ge.setIphSeq(itemRequisicao.getFatItensProcedHospitalar().getId().getSeq());
		codigoGrupo = codigoGrupo + 1;
		listaGrupoExcludentes.add(ge);
		return codigoGrupo;
	}

	/**
	 * Monta quebra dos vos para mostrar na GRID de forma visual
	 * Guardando referencias dos itens mais e filhos
	 * Soma quantidade de qtde solicitada com a quantidade de material
	 * @param opmes
	 */
	private void montaQuebra(List<MbcOpmesVO> opmes, MbcRequisicaoOpmes requisicaoOpmes, List<GrupoExcludenteVO> listaGrupoExcludentes) {
		MbcItensRequisicaoOpmes itemAnterior = null;
		MbcOpmesVO voQuebra = null;
		
		List<MbcOpmesVO> opmesMaterialNulo = new ArrayList<MbcOpmesVO>();
		
		for (MbcOpmesVO mbcOpmesVO : opmes) {
			if (itemAnterior == null || 
					!mbcOpmesVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId()
						.equals(itemAnterior.getFatItensProcedHospitalar().getId())) {
				voQuebra = mbcOpmesVO;
				voQuebra.setFilhos(new ArrayList<MbcOpmesVO>());
				//adiciona filhos para calcular o valor total da quebra em tela
				voQuebra.getFilhos().add(voQuebra);
				//mbcOpmesVO.setVoQuebra(voQuebra);
				requisicaoOpmes.getItensRequisicao().add(mbcOpmesVO.getItensRequisicaoOpmes());
				mbcOpmesVO.setQtdeSol((mbcOpmesVO.getQtdeSol() + mbcOpmesVO.getQtdeSolicitadaMaterial()));
			} else {
				if (mbcOpmesVO.getMaterial() != null) {
					voQuebra.setQtdeSol((voQuebra.getQtdeSol() + mbcOpmesVO.getQtdeSolicitadaMaterial()));
					// adiciona filhos para calcular o valor total da quebra em tela
					voQuebra.getFilhos().add(mbcOpmesVO);
					mbcOpmesVO.setVoQuebra(voQuebra);
					voQuebra.getItensRequisicaoOpmes().getMateriaisItemOpmes().add(mbcOpmesVO.getMbcMateriaisItemOpmes());
					
				} else {
					opmesMaterialNulo.add(mbcOpmesVO);
				}
			}
			
			if (mbcOpmesVO.getVoQuebra() == null) {
				setCorMbcOpmesVO(mbcOpmesVO, listaGrupoExcludentes);
			}
			
			itemAnterior = mbcOpmesVO.getItensRequisicaoOpmes();
		}
		
		opmes.removeAll(opmesMaterialNulo);
	}
	
	private void setCorMbcOpmesVO(MbcOpmesVO mbcOpmesVO, List<GrupoExcludenteVO> listaGrupoExcludentes) {
		for (GrupoExcludenteVO vo : listaGrupoExcludentes) {
			for (ItemProcedimentoVO item : vo.getListaGrupoExcludente()) {
				Integer seq = mbcOpmesVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getSeq();
				Short phoSeq = mbcOpmesVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getPhoSeq();
				if (item.getCmpSeq().equals(seq) && item.getCmpPhoSeq().equals(phoSeq)) {
					mbcOpmesVO.setCor(vo.getCor());
					break;
				}
			}
		}
	}
	
	public MbcOpmesVO criar(MbcItensRequisicaoOpmes item, boolean excluir, Long iphCompCod, String iphCompDescr, MbcMateriaisItemOpmes mbcMateriaisItemOpmes) {
		MbcOpmesVO vo = new MbcOpmesVO();
		vo.setItensRequisicaoOpmes(item);
		vo.setFilhos(new ArrayList<MbcOpmesVO>());

		vo.setExcluir(excluir);

		vo.setQtdeAut((short) 0);
		vo.setQtdeSol( 0);
		vo.setValorUnit(BigDecimal.ZERO);
		vo.setSelecionado(true);
		vo.setQtdeSolicitadaMaterial(0);

		ScoMaterial material = null;
		String codigoDescricao = "";

		if (mbcMateriaisItemOpmes != null) {
			material = mbcMateriaisItemOpmes.getMaterial();
			vo.setMbcMateriaisItemOpmes(mbcMateriaisItemOpmes);
		}
		if (DominioRequeridoItemRequisicao.REQ.equals(item.getRequerido())
				|| DominioRequeridoItemRequisicao.NRQ.equals(item
						.getRequerido())) {
			codigoDescricao = iphCompCod + " - " + iphCompDescr;

		} else if (DominioRequeridoItemRequisicao.ADC.equals(item.getRequerido())
				|| DominioRequeridoItemRequisicao.NOV.equals(item.getRequerido())) {
			if (material == null) {
				vo.setSelecionado(false);
				vo.setQtdeSolicitadaMaterial(0);
			}
			if (DominioRequeridoItemRequisicao.ADC.equals(item.getRequerido())) {
				codigoDescricao = "(Material Adicionado pelo Usuário)";
			} else {
				codigoDescricao = "(Nova Solicitação de Material)";
				vo.setSolicitacaoMaterial(item.getSolicitacaoNovoMaterial());
			}
			
		}
		if (mbcMateriaisItemOpmes != null) {
			vo.setQtdeSolicitadaMaterial(mbcMateriaisItemOpmes.getQuantidadeSolicitada() == null ? 0 : mbcMateriaisItemOpmes.getQuantidadeSolicitada());
		} else {
			vo.setQtdeSolicitadaMaterial(item.getQuantidadeSolicitada().intValue());
		}
		vo.setQtdeSol(item.getQuantidadeSolicitada());

		if (item.getValorUnitarioIph() != null) {
			vo.setValorUnit(item.getValorUnitarioIph());
		}

		vo.setQtdeAut(item.getQuantidadeAutorizadaSus());
		vo.setItemProcedimentoHospitalar(codigoDescricao);
		vo.setMaterial(material);
		return vo;
	}

	private BigDecimal getSomaValoresUnitarios(
			ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO) {
		BigDecimal vlrAnestesia = getValorAnestesia(itemProcedimentoHospitalarMaterialVO);
		
		BigDecimal vlrServHospitalar = getValorServHospitalar(itemProcedimentoHospitalarMaterialVO);
		
		BigDecimal vlrServProfissional = getValorServProfissional(itemProcedimentoHospitalarMaterialVO);
		
		BigDecimal vlrSadt = getValorSadt(itemProcedimentoHospitalarMaterialVO);
		
		BigDecimal vlrProcedimento = getValorProcedimento(itemProcedimentoHospitalarMaterialVO);
		
		BigDecimal vlrUnitario = vlrAnestesia.add(vlrServHospitalar).add(vlrServProfissional).add(vlrSadt).add(vlrProcedimento);
		return vlrUnitario;
	}

	private BigDecimal getValorProcedimento(
			ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO) {
		BigDecimal vlrProcedimento = itemProcedimentoHospitalarMaterialVO.getVlrProcedimento() == null ? BigDecimal.ZERO : itemProcedimentoHospitalarMaterialVO.getVlrProcedimento();
		return vlrProcedimento;
	}

	private BigDecimal getValorSadt(
			ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO) {
		BigDecimal vlrSadt = itemProcedimentoHospitalarMaterialVO.getVlrSadt() == null ? BigDecimal.ZERO : itemProcedimentoHospitalarMaterialVO.getVlrSadt();
		return vlrSadt;
	}

	private BigDecimal getValorServProfissional(
			ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO) {
		BigDecimal vlrServProfissional = itemProcedimentoHospitalarMaterialVO.getVlrServProfissional() == null ? BigDecimal.ZERO : itemProcedimentoHospitalarMaterialVO.getVlrServProfissional();
		return vlrServProfissional;
	}

	private BigDecimal getValorServHospitalar(
			ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO) {
		BigDecimal vlrServHospitalar = itemProcedimentoHospitalarMaterialVO.getVlrServHospitalar() == null ? BigDecimal.ZERO : itemProcedimentoHospitalarMaterialVO.getVlrServHospitalar();
		return vlrServHospitalar;
	}

	private BigDecimal getValorAnestesia(
			ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO) {
		BigDecimal vlrAnestesia = itemProcedimentoHospitalarMaterialVO.getVlrAnestesia() == null ? BigDecimal.ZERO : itemProcedimentoHospitalarMaterialVO.getVlrAnestesia();
		return vlrAnestesia;
	}

	private MbcItensRequisicaoOpmes criaItemRequisicao(
			ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO, MbcRequisicaoOpmes requisicaoOpmes) {
		MbcItensRequisicaoOpmes itensRequisicaoOpmes = new MbcItensRequisicaoOpmes();
		itensRequisicaoOpmes.setFatItensProcedHospitalar(itemProcedimentoHospitalarMaterialVO.getCmp());
		itensRequisicaoOpmes.setRequerido(DominioRequeridoItemRequisicao.NRQ);
		itensRequisicaoOpmes.setIndCompativel(true);
		itensRequisicaoOpmes.setIndAutorizado(true);
		itensRequisicaoOpmes.setIndConsumido(false);
		
		itensRequisicaoOpmes.setQuantidadeAutorizadaSus((short)0);
		itensRequisicaoOpmes.setQuantidadeSolicitada(0);
		itensRequisicaoOpmes.setValorUnitarioIph(BigDecimal.ZERO);
		
		itensRequisicaoOpmes.setRequisicaoOpmes(requisicaoOpmes);
		
		itensRequisicaoOpmes.setMateriaisItemOpmes(new ArrayList<MbcMateriaisItemOpmes>());
		
		return itensRequisicaoOpmes;
	}
	
	public void atualizarRequisicaoCompatibilidade(MbcRequisicaoOpmes requisicao) {
		
		//3. Se foram encontradas incompatibilidades
		requisicao.setIndCompativel(true);
		requisicao.setSituacao(DominioSituacaoRequisicao.COMPATIVEL);
		for (MbcItensRequisicaoOpmes item : requisicao.getItensRequisicao()) {
			if (!item.getIndCompativel()) {
				requisicao.setIndCompativel(false);
				requisicao.setSituacao(DominioSituacaoRequisicao.INCOMPATIVEL);
			}
		}
		requisicao.setIndAutorizado(requisicao.getIndCompativel());
		
	}
	
	private MbcItensRequisicaoOpmesDAO getMbcItensRequisicaoOpmesDAO() {
		return mbcItensRequisicaoOpmesDAO;
	}
	
	private MbcMateriaisItemOpmesDAO getMbcMateriaisItemOpmesDAO() {
		return mbcMateriaisItemOpmesDAO;
	}
	
	public MbcRequisicaoOpmes carregaRequisicao(MbcAgendas agenda) {
		List<MbcRequisicaoOpmes> requisicoes = getMbcRequisicaoOpmesDAO().consultarListaRequisicoesPorAgenda(agenda.getSeq());
		
		if (requisicoes != null && !requisicoes.isEmpty()) {
			return requisicoes.get(0);
		}
		return null;
	}

	public boolean validaExclusao(MbcOpmesVO opmesVO) throws ApplicationBusinessException {
		if (opmesVO.getItensRequisicaoOpmes() != null) {
			if (DominioRequeridoItemRequisicao.NOV.equals(opmesVO.getItensRequisicaoOpmes().getRequerido()) || 
					DominioRequeridoItemRequisicao.ADC.equals(opmesVO.getItensRequisicaoOpmes().getRequerido())) {
				return true;
			} else {
				throw new ApplicationBusinessException(OPMEPortalAgendamentoRNBusinessExceptionCode.MSG_ERRO_EXCL_MAT_PADRAO);
			}
		}
		return false;
		
	}

	public List<MbcOpmesVO> excluir(MbcRequisicaoOpmes requisicaoOpmes, MbcOpmesVO opmesVO, List<MbcOpmesVO> listaPesquisada) {
		requisicaoOpmes.getItensRequisicao().remove(opmesVO.getItensRequisicaoOpmes());
//		for(int i = 0; i < requisicaoOpmes.getItensRequisicao().size(); i++) {
//			if(requisicaoOpmes.getItensRequisicao().get(i).equals(opmesVO.getItensRequisicaoOpmes())){
//				requisicaoOpmes.getItensRequisicao().remove(i);
//				i = requisicaoOpmes.getItensRequisicao().size();
//			}
//		}

		
		//List<MbcOpmesVO> filhos =  opmesVO.getFilhos();
		for(int i = 0; i < listaPesquisada.size(); i++) {	
			if(opmesVO.equals(listaPesquisada.get(i))){
				listaPesquisada.remove(i);
				i = listaPesquisada.size();
			}
		}
		//listaPesquisada.remove(opmesVO);
		//listaPesquisada.removeAll(filhos);
		
		return listaPesquisada;
	}
	
	public void atualizarDetalheMaterial(MbcItensRequisicaoOpmes itemDetalhado){
		itemDetalhado = mbcItensRequisicaoOpmesDAO.merge(itemDetalhado);
		mbcItensRequisicaoOpmesDAO.atualizar(itemDetalhado);
	}
	
	public void gravarMateriaisItemOpmes(MbcMateriaisItemOpmes novoMateriaisItens){
		if(novoMateriaisItens.getSeq() != null){
			novoMateriaisItens.setModificadoEm(new Date());
			novoMateriaisItens.setRapServidoresModificacao(servidorLogadoFacade.obterServidorLogado()); 
			novoMateriaisItens = mbcMateriaisItemOpmesDAO.merge(novoMateriaisItens);
			mbcMateriaisItemOpmesDAO.atualizar(novoMateriaisItens);
		}else{
			mbcMateriaisItemOpmesDAO.persistir(novoMateriaisItens);
		}
	}
	
}
