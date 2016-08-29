package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdtoId;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.model.MpmParecerUsoMdto;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescParecerMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaUsoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmParecerUsoMdtosDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoParecerUsoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoMdtoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class AprovacaoEmLoteRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1948706074738949612L;
	
	private static final String P_TIPO_PARECER_LOTE = "P_TIPO_PARECER_LOTE";
	
	private static final Log LOG = LogFactory.getLog(AprovacaoEmLoteRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;
	
	@Inject
	private MpmItemPrescParecerMdtoDAO mpmItemPrescParecerMdtoDAO;
	
	@Inject
	private MpmTipoParecerUsoMdtoDAO mpmTipoParecerUsoMdtoDAO;
	
	@Inject
	private MpmJustificativaUsoMdtoDAO mpmJustificativaUsoMdtoDAO;
	
	@Inject
	private MpmParecerUsoMdtosDAO mpmParecerUsoMdtosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private MpmJustificativaUsoMdtoRN mpmJustificativaUsoMdtoRN;
	
	@EJB
	private ItemPrescricaoMedicamentoRN itemPrescricaoMedicamentoRN;
	
	@EJB
	private MpmParecerUsoMdtoRN mpmParecerUsoMdtoRN;
	
	@EJB
	private MpmItemPrescParecerMdtoRN mpmItemPrescParecerMdtoRN;
	
	/**
	 * Procedure para retornar os totais de solicitações para aprovação, medicamentos e medicamentos avaliados.
	 * 
	 * @ORADB MPMP_CONTA_JUSTIF
	 * 
	 * @param matricula do usuário logado.
	 * @param vinCodigo do usuário logado.
	 * @param respAvaliacao avaliador selecionado.
	 */
	public Long[] obterContagemTotais(Integer matricula, Short vinCodigo, DominioIndRespAvaliacao respAvaliacao) {

		Long pSolic = 0l;
		Long pItens = 0l;
		Long pComParecer = 0l;
		
		if (respAvaliacao != null) {
			List<Object[]> listaContaJustif = this.mpmItemPrescricaoMdtoDAO.obterContaJustifPorServidorSigla(matricula, vinCodigo, respAvaliacao);
			if (listaContaJustif != null && !listaContaJustif.isEmpty()) {
				for (Object[] c1 : listaContaJustif) {
					pSolic = pSolic + 1;
					pItens = pItens + (Long) c1[1];
				}
				pComParecer = (Long) this.mpmItemPrescricaoMdtoDAO.obterContaParecerPorServidorSigla(matricula, vinCodigo, respAvaliacao);
			}
		} else {
			List<Object[]> listaContaJustifGeral = this.mpmItemPrescricaoMdtoDAO.obterContaJustifGeralPorServidorSigla(matricula, vinCodigo);
			if (listaContaJustifGeral != null && !listaContaJustifGeral.isEmpty()) {
				for (Object[] c1 : listaContaJustifGeral) {
					pSolic = pSolic + 1;
					pItens = pItens + (Long) c1[1];
				}
				pComParecer = (Long) this.mpmItemPrescricaoMdtoDAO.obterContaParecerGeralPorServidorSigla(matricula, vinCodigo);
			}
		}
		
		return new Long[] {pSolic, pItens, pComParecer};
	}
	
	/**
	 * Procedure da ação “Aprovar em Lote” – Cria-se o parecer de uso para cada solicitação encontrada 
	 * e faz atualizações nos medicamentos e justificativas em loop.
	 * 
	 * @ORADB MPMP_APROV_MDTO_LOTE
	 * 
	 * @param matricula do usuário logado.
	 * @param vinCodigo do usuário logado.
	 * @param respAvaliacao avaliador selecionado.
	 * @throws ApplicationBusinessException 
	 */
	public void aprovarLote(Integer matricula, Short vinCodigo, DominioIndRespAvaliacao respAvaliacao) throws ApplicationBusinessException {
		
		BigDecimal vPumSeq = null;
		Integer vAtdSeq = null;
		Long vSeq = null;
		Short vSeqp = null;
		Integer vMedMatCodigo = null;
		Integer vJumSeqAnt = 0;
		BigDecimal vVlrNumerico = this.parametroFacade.obterValorNumericoAghParametros(P_TIPO_PARECER_LOTE);
		
		List<MpmItemPrescricaoMdtoVO> listaItensPrescr = this.mpmItemPrescricaoMdtoDAO.obterItensPrescricaoPorServidorSigla(matricula, vinCodigo, respAvaliacao);
		if (listaItensPrescr != null && !listaItensPrescr.isEmpty()) {
			for (MpmItemPrescricaoMdtoVO fetItensPrescr : listaItensPrescr) {
				
				vAtdSeq = fetItensPrescr.getPmdAtdSeq();
				vSeq = fetItensPrescr.getPmdSeq();
				vSeqp = fetItensPrescr.getSeqp();
				vMedMatCodigo = fetItensPrescr.getMedMatCodigo();
				
				Long count = this.mpmItemPrescParecerMdtoDAO.obterCountParecerPorItemPrescricaoMdto(vAtdSeq, vSeq, vMedMatCodigo, vSeqp);
				
				if (count == null || count == 0l) {
					if (fetItensPrescr.getIndOrigemJustif()) {
						if (!fetItensPrescr.getJumSeq().equals(vJumSeqAnt)) {
							if (!vJumSeqAnt.equals(0)) {
								this.atualizarJustificativaUsoMdto(vJumSeqAnt);
							}
							vJumSeqAnt = fetItensPrescr.getJumSeq();
						}
						MpmParecerUsoMdto pum = new MpmParecerUsoMdto();
						this.inserirParecerUsoMdto(pum, fetItensPrescr.getJumSeq(), vVlrNumerico);
						vPumSeq = pum.getSeq();
					}
					this.inserirItemPrescParecerMdto(vPumSeq, vAtdSeq, vSeq, vSeqp, vMedMatCodigo);
					this.atualizarItemPrescricaoMdto(vAtdSeq, vSeq, vSeqp, vMedMatCodigo, fetItensPrescr.getJumSeq(), fetItensPrescr.getDuracaoTratSolicitado());
				}
			}
			if (!vJumSeqAnt.equals(0)) {
				this.atualizarJustificativaUsoMdto(vJumSeqAnt);
			}
		}
	}
	
	/**
	 * Atualiza Justificativa Uso Medicamento para Avaliada
	 * @param jumSeq
	 * @throws ApplicationBusinessException
	 */
	private void atualizarJustificativaUsoMdto(Integer jumSeq) throws ApplicationBusinessException {
		MpmJustificativaUsoMdto jum = this.mpmJustificativaUsoMdtoDAO.obterPorIdSituacao(jumSeq, DominioSituacaoSolicitacaoMedicamento.T);
		this.mpmJustificativaUsoMdtoRN.atualizarJustificativaUsoMdto(jum, DominioSituacaoSolicitacaoMedicamento.A);
	}
	
	/**
	 * Insere novo Parecer Uso Medicamento
	 * @param pum
	 * @param jumSeq
	 * @param vlrNumerico
	 * @throws ApplicationBusinessException
	 */
	private void inserirParecerUsoMdto(MpmParecerUsoMdto pum, Integer jumSeq, BigDecimal vlrNumerico) 
			throws ApplicationBusinessException {
		pum.setJumSeq(new BigDecimal(jumSeq));
		pum.setMpmTipoParecerUsoMdtos(this.mpmTipoParecerUsoMdtoDAO.obterPorChavePrimaria(vlrNumerico.shortValue()));
		pum.setIndParecerVerificado(DominioSimNao.N);
		this.mpmParecerUsoMdtoRN.persistirParecerUsoMdto(pum);
	}
	
	/**
	 * Insere novo Item Prescricao Parecer Medicamento
	 * @param pumSeq
	 * @param atdSeq
	 * @param seq
	 * @param seqp
	 * @param medMatCodigo
	 * @throws ApplicationBusinessException
	 */
	private void inserirItemPrescParecerMdto(BigDecimal pumSeq, Integer atdSeq, Long seq, Short seqp, Integer medMatCodigo) 
			throws ApplicationBusinessException {
		MpmItemPrescParecerMdto ipr = new MpmItemPrescParecerMdto();
		ipr.setId(new MpmItemPrescParecerMdtoId(atdSeq, seq, medMatCodigo, seqp));
		ipr.setMpmParecerUsoMdtos(this.mpmParecerUsoMdtosDAO.obterPorChavePrimaria(pumSeq));
		this.mpmItemPrescParecerMdtoRN.persistirItemPrescParecerMdto(ipr);
	}

	/**
	 * Atualiza Item Prescricao Medicamento 
	 * @param atdSeq
	 * @param seq
	 * @param seqp
	 * @param medMatCodigo
	 * @param jumSeq
	 * @param duracaoTratSolicitado
	 * @throws ApplicationBusinessException
	 */
	private void atualizarItemPrescricaoMdto(Integer atdSeq, Long seq, Short seqp, Integer medMatCodigo, Integer jumSeq, Short duracaoTratSolicitado) 
			throws ApplicationBusinessException {
		MpmItemPrescricaoMdtoId imeId = new MpmItemPrescricaoMdtoId(atdSeq, seq, medMatCodigo, seqp);
		MpmItemPrescricaoMdto ime = this.mpmItemPrescricaoMdtoDAO.obterPorIdJustificativa(imeId, jumSeq);
		ime.setDuracaoTratAprovado(duracaoTratSolicitado);
		this.itemPrescricaoMedicamentoRN.atualizarItemPrescricaoMedicamento(ime);
	}
}
