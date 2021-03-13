package com.aewinformatica.seguranca.repository.helper.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aewinformatica.seguranca.model.Grupo;
import com.aewinformatica.seguranca.model.Grupo_;
import com.aewinformatica.seguranca.model.Permissao;
import com.aewinformatica.seguranca.model.Permissao_;
import com.aewinformatica.seguranca.model.Usuario;
import com.aewinformatica.seguranca.model.UsuarioGrupo;
import com.aewinformatica.seguranca.model.UsuarioGrupo_;
import com.aewinformatica.seguranca.model.Usuario_;
import com.aewinformatica.seguranca.repository.filter.UsuarioFilter;
import com.aewinformatica.seguranca.repository.paginacao.PaginacaoUtil;

public class UsuariosImpl implements UsuariosQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@Override
	public Optional<Usuario> porEmailEAtivo(String email) {

		Optional<Usuario> usuario = null;

		// construtor
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// retornar uma query para Usuario
		CriteriaQuery<Usuario> criteria = builder.createQuery(Usuario.class);
		// raiz ou origem da busca
		Root<Usuario> root = criteria.from(Usuario.class);

		// criando filtros
		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(builder.equal(root.get(Usuario_.email), email));
		predicates.add(builder.equal(root.get(Usuario_.ativo), true));

		// aonde passa o filtro
		// cria a query com seus parametros
		criteria.select(root).where(predicates.toArray(new Predicate[] {})).distinct(true);

		try {

			TypedQuery<Usuario> query = manager.createQuery(criteria);
			usuario = Optional.of(query.getSingleResult());
		} catch (NoResultException e) {

			e = new NoResultException("Usuario nao encontrado ");
			System.out.println(e.getMessage() + email);
		}

		return usuario;

//		return manager
//				.createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
//				.setParameter("email", email)
//				.getResultList()
//				.stream().findFirst();

	}

	@Override
	public List<String> permissoes(Usuario usuario) throws NoResultException {

		// construtor de criteria
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		// construindo os criterios da pesquisa
		CriteriaQuery<String> criteria = criteriaBuilder.createQuery(String.class).distinct(true);

		Root<Usuario> root = criteria.from(Usuario.class);
		Join<Usuario, Grupo> grupos = root.join(Usuario_.grupos);// fazendo os joins
		Join<Grupo, Permissao> permissoes = grupos.join(Grupo_.permissoes);// fazendo os joins

		TypedQuery<String> typedQuery = manager.createQuery(criteria.multiselect(permissoes.get(Permissao_.nome))// o
																													// que
																													// pegar
				.where(criteriaBuilder.equal(root.get(Usuario_.codigo), usuario.getCodigo())));

		List<String> listaPermissoes = typedQuery.getResultList();

		return listaPermissoes;

		/*
		 * return manager.createQuery( "select distinct p.nome from Usuario u " +
		 * "inner join u.grupos g " + "inner join g.permissoes p" +
		 * " where u = :usuario", String.class) .setParameter("usuario", usuario)
		 * .getResultList();
		 */
	}

	@Transactional(readOnly = true)
	@Override
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable) {
		// construtor
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// retornar uma query para Usuario
		CriteriaQuery<Usuario> criteria = builder.createQuery(Usuario.class);
		// raiz ou origem da busca
		Root<Usuario> root = criteria.from(Usuario.class);
		root.join(Usuario_.grupos, JoinType.LEFT).alias("grupos");

//		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, builder, root);

		TypedQuery<Usuario> query = manager.createQuery(criteria);
		paginacaoUtil.adicionarRestricoesDePaginacao(query, pageable);

		List<Usuario> filtrados = query.getResultList();
		filtrados.forEach(u -> Hibernate.initialize(u.getGrupos()));

		return new PageImpl<>(filtrados, pageable, total(filtro));
	}

	@Transactional(readOnly = true)
	@Override

	public Usuario buscarComGrupos(Long codigo) {

		Usuario usuarioEncontrado = null;
		// construtor
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// retornar uma query para Usuario
		CriteriaQuery<Usuario> criteria = builder.createQuery(Usuario.class);
		// raiz ou origem da busca
		Root<Usuario> root = criteria.from(Usuario.class);
		root.join(Usuario_.grupos, JoinType.LEFT).alias("grupos");

		// criando filtros
		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(builder.equal(root.get(Usuario_.codigo), codigo));

		// aonde passa o filtro
		// cria a query com seus parametros
		criteria.select(root).where(predicates.toArray(new Predicate[] {})).distinct(true);

		try {

			TypedQuery<Usuario> query = manager.createQuery(criteria);
			usuarioEncontrado = query.getSingleResult();
		} catch (NoResultException e) {

			e = new NoResultException("Usuario nao encontrado ");
			System.out.println(e.getMessage() + codigo);
		}

		return usuarioEncontrado;


		/*Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("grupos", "g", org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);		
		return (Usuario) criteria.uniqueResult();*/
	}

	private Long total(UsuarioFilter filtro) {

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

		Root<Usuario> root = criteria.from(Usuario.class);

		Predicate[] predicates = adicionarFiltro(filtro, builder, root);
		criteria.where(predicates);

		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

	private Predicate[] adicionarFiltro(UsuarioFilter filtro, CriteriaBuilder builder,Root<Usuario>root) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(builder.lower(root.get("nome")),"%" + filtro.getNome().toLowerCase() + "%"));
			}
			
			if (!StringUtils.isEmpty(filtro.getEmail())) {
				predicates.add(builder.like(builder.lower(root.get("email")),"%" + filtro.getEmail().toLowerCase()));
			}
			
			
			if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
				
				// construindo os criterios da pesquisa
			  	              CriteriaQuery<UsuarioGrupo> criteria = builder.createQuery(UsuarioGrupo.class);
				Root<UsuarioGrupo> rootFromUsuarioGrupo = criteria.from(UsuarioGrupo.class);

				for (Long codigoGrupo : filtro.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
					predicates.add(builder.equal(rootFromUsuarioGrupo.get("id.grupo.codigo"), codigoGrupo));
					System.out.println(">>CODIGO DO GRUPO" + codigoGrupo);
					
				}
				
			}
			
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}