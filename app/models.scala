package models
 
import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm.SqlParser._

import java.util.{Date}
 
case class User(
    id: Pk[Long], 
    email: String, password: String, fullname: String, isAdmin: Boolean
)

object User extends Magic[User] {
    
    def connect(email: String, password: String) = {
        User.find("email = {email} and password = {password}")
            .on("email" -> email, "password" -> password)
            .first()
    }
    
}

case class Post(
    id: Pk[Long], 
    title: String, content: String, postedAt: Date, author_id: Long
) {
    
    def prevNext = {        
        SQL(
            """
                (
                    select *, 'next' as pos from post 
                    where postedAt < {date} order by postedAt desc limit 1
                )
                    union
                (
                    select *, 'prev' as pos from post 
                    where postedAt > {date} order by postedAt asc limit 1
                )

                order by postedAt desc

            """
        ).on("date" -> postedAt).as( 
            opt('pos.is("prev")~>Post.on("")) ~ opt('pos.is("next")~>Post.on("")) 
        )
    }
    
}
 
object Post extends Magic[Post] {
    
    def allWithAuthor:List[(Post,User)] = 
        SQL(
            """
                select * from Post p 
                join User u on p.author_id = u.id 
                order by p.postedAt desc
            """
        ).as( Post ~< User ^^ flatten * )
        
    def allWithAuthorAndComments:List[(Post,User,List[Comment])] = 
        SQL(
            """
                select * from Post p 
                join User u on p.author_id = u.id 
                left join Comment c on c.post_id = p.id 
                order by p.postedAt desc
            """
        ).as( Post ~< User ~< Post.spanM( Comment ) ^^ flatten * )
        
    def byIdWithAuthorAndComments(id: Long):Option[(Post,User,List[Comment])] = 
        SQL(
            """
                select * from Post p 
                join User u on p.author_id = u.id 
                left join Comment c on c.post_id = p.id 
                where p.id = {id}
            """
        ).on("id" -> id).as( Post ~< User ~< Post.spanM( Comment ) ^^ flatten ? )
    
}

case class Comment(
    id: Pk[Long], 
    author: String, content: String, postedAt: Date, post_id: Long
) 
 
object Comment extends Magic[Comment] {
    
    def apply(post_id: Long, author: String, content: String) = {
        new Comment(NotAssigned, author, content, new Date(), post_id)
    }
    
}
class Point(val name: String, val value: Long)

case class Environment(id: Pk[Long], name: String)
object Environment extends Magic[Environment] {
  def findAll = find().as(Environment *)
}

case class WebloDomain(id: Pk[Long], name: String, host: String, port: String, username: String, password: String, env_id: Long)
object WebloDomain extends Magic[WebloDomain] {

  def withServers(): List[(WebloDomain, List[WebloServer])] = {
    SQL(
        """
            select * from WebloDomain d
            left join WebloServer s on s.domain_id = d.id
            order by d.name asc
        """
    ).as( WebloDomain ~< WebloDomain.spanM(WebloServer) ^^ flatten *)

    // flatten permet de convertir WebloDomain~List[WebloServer] en (WebloDomain, List[WebloServer])
  }

  def withServers(id: Long): Option[(WebloDomain, List[WebloServer])] = {
    SQL(
        """
            select * from WEBLODOMAIN d
            left join WEBLOSERVER s on s.domain_id = d.id
            where d.id={id}
        """
    ).on("id" -> id).as( (WebloDomain ~< WebloDomain.spanM(WebloServer)) ^^ flatten ?)

    // flatten permet de convertir WebloDomain~List[WebloServer] en (WebloDomain, List[WebloServer])
  }
}

case class WebloServer(id: Pk[Long], name: String, host: String, port: String, domain_id: Long)
object WebloServer extends Magic[WebloServer]